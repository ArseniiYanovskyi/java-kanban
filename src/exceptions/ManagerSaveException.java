package exceptions;

public class ManagerSaveException extends RuntimeException{
    private String message;
    public ManagerSaveException(String message){
        this.message = message;
    }
    public String getMessage(){
        return message;
    }
}
