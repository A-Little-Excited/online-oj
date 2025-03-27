package com.excited.friend.service.impl;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.excited.common.core.constants.CacheConstants;
import com.excited.common.core.constants.Constants;
import com.excited.common.core.enums.ResultCode;
import com.excited.common.core.enums.UserIdentity;
import com.excited.common.core.enums.UserStatus;
import com.excited.common.message.service.AliSmsService;
import com.excited.common.redis.service.RedisService;
import com.excited.common.security.exception.ServiceException;
import com.excited.common.security.service.JwtService;
import com.excited.friend.domain.dto.UserGetCodeDTO;
import com.excited.friend.domain.dto.UserLoginDTO;
import com.excited.friend.domain.entity.User;
import com.excited.friend.mapper.UserMapper;
import com.excited.friend.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class UserServiceImpl implements IUserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private AliSmsService aliSmsService;

    @Autowired
    private RedisService redisService;

    @Autowired
    private JwtService jwtService;

    // 验证码有效时间, 默认值为 5, 单位为分钟
    @Value("${sms.code.exp:5}")
    private Long phoneCodeExp;

    // 每天验证码获取次数上限, 默认值为 3
    @Value("${sms.code.times-limit:3}")
    private Long codeTimesLimit;

    @Value("${jwt.secret}")
    private String secret;

    // 是否发送短信: 如果是本地开发进行测试则不进行发送, 项目正式上线后才进行发送
    @Value("${sms.is-send:false}")
    private boolean isSend;

    @Override
    public boolean sendCode(UserGetCodeDTO userGetCodeDTO) {
        // 1. 对手机号码进行校验
        if (!checkPhone(userGetCodeDTO.getPhone())) {
            throw new ServiceException(ResultCode.FAILED_USER_PHONE);
        }

        // 2. 对获取验证码进行时间间隔限制: 限制两次获取验证码的时间间隔至少为一分钟
        String phoneCodeKey = getPhoneCodeKey(userGetCodeDTO.getPhone());
        Long expire = redisService.getExpire(phoneCodeKey, TimeUnit.SECONDS);
        if (expire != null && (phoneCodeExp * 60 - expire) < 60) {
            throw new ServiceException(ResultCode.FAILED_FREQUENT);
        }

        // 3. 对获取验证码进行每天次数限制
        String codeTimesKey = getCodeTimesKey(userGetCodeDTO.getPhone());
        Long codeTimes = redisService.getCacheObject(codeTimesKey, Long.class);
        if (codeTimes != null && codeTimes >= codeTimesLimit) {
            throw new ServiceException(ResultCode.FAILED_TIMES_LIMIT);
        }

        // 4. 获取验证码: 随机生成一个 6 位的数字作为验证码
        // 本地测试时就使用默认的验证码
        String code = isSend ? RandomUtil.randomNumbers(6) : Constants.DEFAULT_CODE;
        // 5. 将验证码存储在 Redis 中
        redisService.setCacheObject(phoneCodeKey, code,
                phoneCodeExp, TimeUnit.MINUTES);
        // 6. 将验证码发送给用户
        // 本地测试就不进行短信发送
        if (isSend) {
            boolean sendResult = aliSmsService.sendCode(userGetCodeDTO.getPhone(), code);
            if (!sendResult) {
                throw new ServiceException(ResultCode.FAILED_SEND_CODE);
            }
        }
        // 7. 用户成功获取验证码, 当天请求验证码次数 +1
        // 如果 codeTimesKey 不存在, 那么 Redis 也会先创建一个该 Key 再进行自增, 也即是无需再手动创建该键值对
        redisService.increment(codeTimesKey);

        // 8. 如果是当天第一次请求验证码, 需要设置第二天凌晨时前一天计数自动失效
        // 由于 codeTimes 是在自增操作前获取的, 因此如果是第一次请求验证码, 该值为空
        if (codeTimes == null) {
            // 自动计算当前时间距离第二天凌晨的时间差, 以此作为"次数"的有效时间
            long seconds = ChronoUnit.SECONDS.between(LocalDateTime.now(),
                    LocalDateTime.now().plusDays(1).withHour(0).withMinute(0).withSecond(0).withNano(0));
            redisService.expire(codeTimesKey, seconds, TimeUnit.SECONDS);
        }

        return true;
    }

    @Override
    public String codeLogin(UserLoginDTO userLoginDTO) {
        // 无论是新用户还是老用户, 登录或注册之前都需要先比较验证码是否正确
        // 只有匹配当前手机号的验证码才可以进行登录或注册
        String phone = userLoginDTO.getPhone();
        String code = userLoginDTO.getCode();
        checkCode(phone, code);

        User user = userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getPhone, phone));
        // 新用户需要先进行注册
        if (user == null) {
            user = new User();
            user.setPhone(phone);
            user.setStatus(UserStatus.Normal.getStatus());
            userMapper.insert(user);
        }
        // 老用户直接执行登录
        // 为用户生成 token
        return jwtService.createToken(user.getUserId(), secret,
                UserIdentity.ORDINARY.getValue(), user.getNickName());
    }

    private void checkCode(String phone, String code) {
        String phoneCodeKey = getPhoneCodeKey(phone);
        // 1. 先获取 Redis 中存储的验证码
        String cacheCode = redisService.getCacheObject(phoneCodeKey, String.class);
        if (StrUtil.isEmpty(cacheCode)) {
            // 验证码过期
            throw new ServiceException(ResultCode.FAILED_INVALID_CODE);
        }
        if (!cacheCode.equals(code)) {
            // 验证码错误
            throw new ServiceException(ResultCode.FAILED_ERROR_CODE);
        }
        // 2. 验证码校验成功后, 需要将 Redis 中存储的验证码删除
        redisService.deleteObject(phoneCodeKey);
    }

    private static boolean checkPhone(String phone) {
        Pattern pattern = Pattern.compile("^1[2|3|4|5|6|7|8|9][0-9]\\d{8}$");
        Matcher matcher = pattern.matcher(phone);
        return matcher.matches();
    }

    private String getPhoneCodeKey(String phone) {
        // key: p:c: + phone
        // value: 验证码
        return CacheConstants.PHONE_CODE_KEY_PREFIX + phone;
    }

    private String getCodeTimesKey(String phone) {
        // key: c:t: + phone
        // value: 次数
        return CacheConstants.CODE_TIMES_KEY_PREFIX + phone;
    }
}
