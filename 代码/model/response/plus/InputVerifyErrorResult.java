package chang.model.response.plus;

import chang.model.response.ResponseResult;
import chang.model.response.code.CommonCode;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class InputVerifyErrorResult extends ResponseResult {

    private String error_msg;

    public InputVerifyErrorResult(String error_msg){
        super(CommonCode.INVALID_PARAM);
        this.error_msg = error_msg;
    }

}
