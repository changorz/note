package chang.model.exception;

import chang.model.response.code.ResultCode;

/**
 * 自定义异常类型
 * @author Administrator
 * @version 1.0
 * @create 2018-09-14 17:28
 **/
public class MyException extends RuntimeException {

    // 错误代码
    ResultCode resultCode;

    public MyException(ResultCode resultCode){
        this.resultCode = resultCode;
    }
    public ResultCode getResultCode(){
        return resultCode;
    }


}
