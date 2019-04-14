package challenge.springproject.dto.input;

public class PhoneDto {
    private String number;
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
