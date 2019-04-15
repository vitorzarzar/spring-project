package challenge.springproject.dto.input;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

public class PhoneDto {

    @Max(9)
    @Min(8)
    @NotBlank
    private String number;

    @Max(3)
    @NotBlank
    private String ddd;

    public PhoneDto() {
    }

    public PhoneDto(String ddd, String number) {
        this.number = number;
        this.ddd = ddd;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getDdd() {
        return ddd;
    }

    public void setDdd(String ddd) {
        this.ddd = ddd;
    }
}
