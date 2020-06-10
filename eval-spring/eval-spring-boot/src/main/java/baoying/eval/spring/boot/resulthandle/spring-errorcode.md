- 关于error code（和成果处理）的处理，理论上尽量按照http code返回，然后加上业务的字段
  - 但是实际处理的时候，每个公司/产品的业务不同，实际运用起来是都是有差别的
  - 这篇文章写的很实在： https://zhuanlan.zhihu.com/p/93760159
    - "还是那句话：领导用啥咱用啥，同事用啥咱用啥，老代码用啥咱用啥，最后才是，我习惯用啥就用啥。"
- 本文中推荐的用法
  - http-code按照下面大致分类
    - 200/成功。别整什么201/202之类的，弄错了还麻烦，除非有特别的业务需求再去整理
    - 400/失败1-client端问题，譬如数据验证失败、用户没有权限等
    - 500/失败2-server端问题。
    - 500/失败3-其他问题，还是500，主要指那些server 内部的runtime exception
  - http-body
```
{
    "status"  : [OK|ERROR]
    "err_code": 12345 #对于status为OK情况下，应该为0或者空
    "err_msg" : ""    #对于status为OK情况下，应该为空
    
    #业务返回的数据放在这里
    data: {
        new_ord_response{
            order_id: "",
            client_id:""，
            client_name:"",
            calculated_px:"",
        }
    }
}

```
    - error-code
      - 正常情况下error-code为空，只有有问题才出现
      - 业务相关的error code
      - 参考微信返回码 https://developers.weixin.qq.com/doc/oplatform/Return_codes/Return_code_descriptions.html
      - 参考阿里返回码 https://opendocs.alipay.com/open/common/105806
    - 统一格式, 每个公司可能不同，但是大致上都这样设定
      - 可以参考各大公司的API
      - btw：微信支付接口返回的是xml，不过异曲同工啦，这里有例子 https://pay.weixin.qq.com/wiki/doc/api/micropay.php?chapter=9_2
    - 支付宝返回例子 https://opendocs.alipay.com/open/common/105816
```
{
	"monitor_heartbeat_syn_response": {
		"code": "10000",
		"msg": "同步心跳信息成功"
	}
}

{
	"monitor_heartbeat_syn_response": {
		"code": "40004",
		"sub_code":"ILLEGAL_SIGN",
		"msg":"接口调用异常",
		"sub_desc":"签名不正确"
	}
}
```
  - 我原来（2018）一直希望在全局放一个clientID(在最顶层与err_code并列或者作为data的直接子节点），用于保存客户送进来的id，方便日后问题调查
    - 但是，进行错误数据处理的时候，获得这个clientID的值，再附值可能又会有点麻烦
    - 所以，我觉得就尽量给值把。由于REST是request/response类型，根据log情况，检查起来应该也还好
    - 另外，可能这个放在业务里面更格式，参考FIX消息中的ClientOrdId
