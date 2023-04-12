public class ManagerSaveException extends Exception{
    private String message;
    public ManagerSaveException(String message){
        this.message = message;
    }
    public String getMessage(){
        return message;
    }
}
