package TaskData;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

public class Task {
    protected enum Status{
        NEW,
        IN_PROGRESS,
        DONE
    }
    protected Instant startTime;
    protected Instant duration;
    protected Instant endTime;
    static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm, dd.MM.yyyy");
    protected Status status;
    private String title;
    private String description;
    private int id;

    public Task(String title, String description){
        this.title = title;
        this.description = description;
        this.status = Status.NEW;
        this.id = 0;
    }

    public Task(String title, String description, int id){
        this.title = title;
        this.description = description;
        this.status = Status.NEW;
        this.id = id;
    }

    public void resetTimeValues(){
        this.startTime = null;
        this.duration = null;
        this.endTime = null;
    }

    public void setStartTime(Instant startTime){
        this.startTime = startTime;
        if(duration != null){
            endTime = Instant.ofEpochMilli(startTime.toEpochMilli() + duration.toEpochMilli());
        }
    }

    public void setDuration(Instant duration){
        this.duration = duration;
        if(startTime != null){
            endTime = Instant.ofEpochMilli(startTime.toEpochMilli() + duration.toEpochMilli());
        }
    }

    public void setStatus(String status){
        this.status = Status.valueOf(status);
    }

    public String getStatus() {
        return status.toString();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getStartTimeOfMillis(){
        return startTime != null ? duration.toEpochMilli() : 0;
    }

    public long getDurationOfMillis(){
        return duration != null ? duration.toEpochMilli() : 0;
    }

    public Optional<Instant> getOptionalOfStartTime() {
        return Optional.ofNullable(startTime);
    }

    public Instant getStartTime(){
        return this.startTime;
    }
    public Instant getEndTime(){
        return this.endTime;
    }
    public Optional<Instant> getOptionalOfEndTime() {
        return Optional.ofNullable(endTime);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o == null || o.getClass() != o.getClass()) {
            return false;
        }

        return (this.id == ((Task)o).getId() &&
                this.status.equals(((Task)o).getStatus()) &&
                this.title.equals(((Task)o).getTitle()) &&
                this.description.equals(((Task)o).getDescription()));
    }
    public void printInfo(){
        if (startTime != null || duration != null) {
            System.out.println("ID задачи - '" + id + '\''
                    + "\nТекущий статус задачи - '" + status + '\''
                    + "\nНазвание задачи - '" + title + '\''
                    + "\nОписание задачи - '" + description + '\''
                    + "\nОриентировочное время начала выполнения задачи - '"
                    + LocalDateTime.ofInstant(startTime, ZoneId.of("Europe/Moscow")).format(formatter) + '\''
                    + "\nОриентировочное время завершения задачи - '"
                    + LocalDateTime.ofInstant(endTime, ZoneId.of("Europe/Moscow")).format(formatter) + '\'');
        } else {
            System.out.println("ID задачи - '" + id + '\''
                    + "\nТекущий статус задачи - '" + status + '\''
                    + "\nНазвание задачи - '" + title + '\''
                    + "\nОписание задачи - '" + description + '\''
                    + "\nОриентировочное время начала выполнения задачи - неизвестно"
                    + "\nОриентировочное время завершения задачи - неизвестно");
        }
    }
}
