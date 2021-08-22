package cn.lianshi.thirdlib.pojo;

import com.google.gson.annotations.SerializedName;


/**
 * @author sqk
 * @email songqk316@163.com
 * @data on 2018/10/15/015
 * @描述：微信支付实体,也可通过msg获取阿里支付orderid
 */
public class WeChatPayEntity {

    /**
     * status : suc
     * prepayId : wx1515001895996811c6385ff34221990041
     * timeStamp : 1539586818
     * nonceStr : Q8TIaYMc4hjeIsyn
     * paySign : 0928591902EE379FCFCA9A3832E29AE9
     * package : Sign=WXPay
     */

    private String status;
    private String prepayId;
    private String timeStamp;
    private String nonceStr;
    private String paySign;
    @SerializedName("package")
    private String packageX;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPrepayId() {
        return prepayId;
    }

    public void setPrepayId(String prepayId) {
        this.prepayId = prepayId;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getNonceStr() {
        return nonceStr;
    }

    public void setNonceStr(String nonceStr) {
        this.nonceStr = nonceStr;
    }

    public String getPaySign() {
        return paySign;
    }

    public void setPaySign(String paySign) {
        this.paySign = paySign;
    }

    public String getPackageX() {
        return packageX;
    }

    public void setPackageX(String packageX) {
        this.packageX = packageX;
    }
}
