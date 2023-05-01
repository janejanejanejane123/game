package com.ruoyi.common.core.domain.model.member;

import com.ruoyi.common.bussiness.constants.Constant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

/**
 * 【请填写功能名称】对象 t_user
 * 
 * @author ruoyi
 * @date 2022-03-21
 */
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TUser extends BaseEntity
{
    private static final long serialVersionUID = 1L;


    private Long merchantId;

    public Long getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(Long merchantId) {
        this.merchantId = merchantId;
    }

    private String lastLoginIp;

    private String lastLoginCity;

    public String getLastLoginIp() {
        return lastLoginIp;
    }

    public void setLastLoginIp(String lastLoginIp) {
        this.lastLoginIp = lastLoginIp;
    }

    public String getLastLoginCity() {
        return lastLoginCity;
    }

    public void setLastLoginCity(String lastLoginCity) {
        this.lastLoginCity = lastLoginCity;
    }

    private String url;

    private Long uniqueCode;

    private Long pid;

    private String pidArray;

    private short type;

    /** $column.columnComment */
    @Excel(name = "${comment}", readConverterExp = "$column.readConverterExp()")
    @Pattern(regexp = Constant.USERNAME_PATTEN,message = "member.info.usernamePatternError")
    private String username;

    /** $column.columnComment */
    @Excel(name = "${comment}", readConverterExp = "$column.readConverterExp()")
    @Pattern(regexp = Constant.PASSWORD_PATTEN,message = "member.info.passwordPatternError")
    private String password;

    /** $column.columnComment */
    private Long uid;

    /** $column.columnComment */
    @Excel(name = "${comment}", readConverterExp = "$column.readConverterExp()")
    @Pattern(regexp = Constant.REAL_NAME,message = "member.info.realName.patternError")
    @NotNull
    private String realname;

    /** $column.columnComment */
    @Excel(name = "${comment}", readConverterExp = "$column.readConverterExp()")
    private String photo;

    /** $column.columnComment */
    @Excel(name = "${comment}", readConverterExp = "$column.readConverterExp()")
    private String payPassword;

    /** $column.columnComment */
    @Excel(name = "${comment}", readConverterExp = "$column.readConverterExp()")
    private String nickname;

    /** $column.columnComment */
    @Excel(name = "${comment}", readConverterExp = "$column.readConverterExp()")
    private Long userLevel;

    /** $column.columnComment */
    @Excel(name = "${comment}", readConverterExp = "$column.readConverterExp()")
    private String salt;

    /** $column.columnComment */
    @Excel(name = "${comment}", readConverterExp = "$column.readConverterExp()")
    private Long disabled;

    /** $column.columnComment */
    @Excel(name = "${comment}", readConverterExp = "$column.readConverterExp()")
//    @Pattern(regexp = Constant.EMAIL_PATTEN,message = "member.info.email.patternError")
    private String email;

    /** $column.columnComment */
    @Excel(name = "${comment}", readConverterExp = "$column.readConverterExp()")
    @Pattern(regexp = Constant.PHONE_NUMBER_REG,message = "member.info.phone.patternError")
    private String telephone;

    public Short getVerifiedRealName() {
        return verifiedRealName;
    }

    public void setVerifiedRealName(Short verifiedRealName) {
        this.verifiedRealName = verifiedRealName;
    }

    public Long getPid() {
        return pid;
    }

    public void setPid(Long pid) {
        this.pid = pid;
    }

    public String getPidArray() {
        return pidArray;
    }

    public void setPidArray(String pidArray) {
        this.pidArray = pidArray;
    }

    public short getType() {
        return type;
    }

    public void setType(short type) {
        this.type = type;
    }

    private Short verifiedRealName;

    public void setUsername(String username) 
    {
        this.username = username;
    }

    public String getUsername() 
    {
        return username;
    }
    public void setPassword(String password) 
    {
        this.password = password;
    }

    public String getPassword() 
    {
        return password;
    }
    public void setUid(Long uid) 
    {
        this.uid = uid;
    }

    public Long getUid() 
    {
        return uid;
    }
    public void setRealname(String realname) 
    {
        this.realname = realname;
    }

    public String getRealname() 
    {
        return realname;
    }
    public void setPhoto(String photo) 
    {
        this.photo = photo;
    }

    public String getPhoto() 
    {
        return photo;
    }
    public void setPayPassword(String payPassword) 
    {
        this.payPassword = payPassword;
    }

    public String getPayPassword() 
    {
        return payPassword;
    }
    public void setNickname(String nickname) 
    {
        this.nickname = nickname;
    }

    public String getNickname() 
    {
        return nickname;
    }
    public void setUserLevel(Long userLevel) 
    {
        this.userLevel = userLevel;
    }

    public Long getUserLevel() 
    {
        return userLevel;
    }
    public void setSalt(String salt) 
    {
        this.salt = salt;
    }

    public String getSalt() 
    {
        return salt;
    }
    public void setDisabled(Long disabled) 
    {
        this.disabled = disabled;
    }

    public Long getDisabled() 
    {
        return disabled;
    }
    public void setEmail(String email) 
    {
        this.email = email;
    }

    public String getEmail() 
    {
        return email;
    }
    public void setTelephone(String telephone) 
    {
        this.telephone = telephone;
    }

    public String getTelephone() 
    {
        return telephone;
    }

    public Long getUniqueCode() {
        return uniqueCode;
    }

    public void setUniqueCode(Long uniqueCode) {
        this.uniqueCode = uniqueCode;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("username", getUsername())
            .append("password", getPassword())
            .append("uid", getUid())
            .append("realname", getRealname())
            .append("photo", getPhoto())
            .append("payPassword", getPayPassword())
            .append("nickname", getNickname())
            .append("userLevel", getUserLevel())
            .append("salt", getSalt())
            .append("disabled", getDisabled())
            .append("email", getEmail())
            .append("telephone", getTelephone())
            .toString();
    }
}
