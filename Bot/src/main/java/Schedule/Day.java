package Schedule;


public class Day {
    
    final String date;
    final String dateNum;
    
    public Day(String date, String dateNum){
        this.date = date;
        this.dateNum = dateNum;
    }
    
    @Override
    public String toString(){
        String res = "<<< " + date + " " + dateNum + " >>>\n\n";
        return res + "[В этот день нет пар]";
    }

    @Override
    public Day clone() {
        return this;
    }
}
