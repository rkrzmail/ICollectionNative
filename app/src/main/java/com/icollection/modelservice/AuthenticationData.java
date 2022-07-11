package com.icollection.modelservice;

/**
 * Created by user on 1/10/2018.
 */




        import com.google.gson.annotations.Expose;
        import com.google.gson.annotations.SerializedName;

public class AuthenticationData {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("pass")
    @Expose
    private String pass;
    @SerializedName("username")
    @Expose
    private String username;
    @SerializedName("full_name")
    @Expose
    private String fullName;
    @SerializedName("avatar")
    @Expose
    private String avatar;
    @SerializedName("banned")
    @Expose
    private String banned;
    @SerializedName("last_login")
    @Expose
    private String lastLogin;
    @SerializedName("last_activity")
    @Expose
    private String lastActivity;
    @SerializedName("date_created")
    @Expose
    private String dateCreated;
    @SerializedName("forgot_exp")
    @Expose
    private Object forgotExp;
    @SerializedName("remember_time")
    @Expose
    private Object rememberTime;
    @SerializedName("remember_exp")
    @Expose
    private Object rememberExp;
    @SerializedName("verification_code")
    @Expose
    private Object verificationCode;
    @SerializedName("top_secret")
    @Expose
    private Object topSecret;
    @SerializedName("ip_address")
    @Expose
    private String ipAddress;

    /**
     * No args constructor for use in serialization
     *
     */
    public AuthenticationData() {
    }

    /**
     *
     * @param topSecret
     * @param rememberExp
     * @param avatar
     * @param banned
     * @param rememberTime
     * @param pass
     * @param id
     * @param username
     * @param lastLogin
     * @param lastActivity
     * @param email
     * @param verificationCode
     * @param dateCreated
     * @param fullName
     * @param forgotExp
     * @param ipAddress
     */
    public AuthenticationData(String id, String email, String pass, String username, String fullName, String avatar, String banned, String lastLogin, String lastActivity, String dateCreated, Object forgotExp, Object rememberTime, Object rememberExp, Object verificationCode, Object topSecret, String ipAddress) {
        super();
        this.id = id;
        this.email = email;
        this.pass = pass;
        this.username = username;
        this.fullName = fullName;
        this.avatar = avatar;
        this.banned = banned;
        this.lastLogin = lastLogin;
        this.lastActivity = lastActivity;
        this.dateCreated = dateCreated;
        this.forgotExp = forgotExp;
        this.rememberTime = rememberTime;
        this.rememberExp = rememberExp;
        this.verificationCode = verificationCode;
        this.topSecret = topSecret;
        this.ipAddress = ipAddress;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public AuthenticationData withId(String id) {
        this.id = id;
        return this;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public AuthenticationData withEmail(String email) {
        this.email = email;
        return this;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public AuthenticationData withPass(String pass) {
        this.pass = pass;
        return this;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public AuthenticationData withUsername(String username) {
        this.username = username;
        return this;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public AuthenticationData withFullName(String fullName) {
        this.fullName = fullName;
        return this;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public AuthenticationData withAvatar(String avatar) {
        this.avatar = avatar;
        return this;
    }

    public String getBanned() {
        return banned;
    }

    public void setBanned(String banned) {
        this.banned = banned;
    }

    public AuthenticationData withBanned(String banned) {
        this.banned = banned;
        return this;
    }

    public String getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(String lastLogin) {
        this.lastLogin = lastLogin;
    }

    public AuthenticationData withLastLogin(String lastLogin) {
        this.lastLogin = lastLogin;
        return this;
    }

    public String getLastActivity() {
        return lastActivity;
    }

    public void setLastActivity(String lastActivity) {
        this.lastActivity = lastActivity;
    }

    public AuthenticationData withLastActivity(String lastActivity) {
        this.lastActivity = lastActivity;
        return this;
    }

    public String getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(String dateCreated) {
        this.dateCreated = dateCreated;
    }

    public AuthenticationData withDateCreated(String dateCreated) {
        this.dateCreated = dateCreated;
        return this;
    }

    public Object getForgotExp() {
        return forgotExp;
    }

    public void setForgotExp(Object forgotExp) {
        this.forgotExp = forgotExp;
    }

    public AuthenticationData withForgotExp(Object forgotExp) {
        this.forgotExp = forgotExp;
        return this;
    }

    public Object getRememberTime() {
        return rememberTime;
    }

    public void setRememberTime(Object rememberTime) {
        this.rememberTime = rememberTime;
    }

    public AuthenticationData withRememberTime(Object rememberTime) {
        this.rememberTime = rememberTime;
        return this;
    }

    public Object getRememberExp() {
        return rememberExp;
    }

    public void setRememberExp(Object rememberExp) {
        this.rememberExp = rememberExp;
    }

    public AuthenticationData withRememberExp(Object rememberExp) {
        this.rememberExp = rememberExp;
        return this;
    }

    public Object getVerificationCode() {
        return verificationCode;
    }

    public void setVerificationCode(Object verificationCode) {
        this.verificationCode = verificationCode;
    }

    public AuthenticationData withVerificationCode(Object verificationCode) {
        this.verificationCode = verificationCode;
        return this;
    }

    public Object getTopSecret() {
        return topSecret;
    }

    public void setTopSecret(Object topSecret) {
        this.topSecret = topSecret;
    }

    public AuthenticationData withTopSecret(Object topSecret) {
        this.topSecret = topSecret;
        return this;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public AuthenticationData withIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
        return this;
    }

}
