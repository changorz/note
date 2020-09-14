package chang.model.exception;

import chang.model.response.code.ResultCode;

/**
 * @author Administrator
 * @version 1.0
 * @create 2018-09-14 17:31
 **/
public class ExceptionCast {

    public static void cast(ResultCode resultCode){
        throw new MyException(resultCode);
    }
}
