package challenge.springproject.dto.output;

public class ExceptionOutputDto {

    private String mensage;

    public ExceptionOutputDto(String mensage) {
        this.mensage = mensage;
    }

    public String getMensage() {
        return mensage;
    }

    public void setMensage(String mensage) {
        this.mensage = mensage;
    }
}
