import java.util.ArrayList;
import java.util.List;

class LogString {

    private String date;
    private String count;
    private String difference;

    LogString(String string) {
        this.date = parceLogString(string).get(0);
        this.count = parceLogString(string).get(1);
        this.difference = parceLogString(string).get(2);
    }

    private List<String> parceLogString(String string) {
        List<String> list = new ArrayList<>();
        int firstSpace = string.indexOf(" ");
        int secondSpace = string.lastIndexOf(" ");

        list.add(string.substring(0, 10));
        if (firstSpace == secondSpace) {
            list.add(string.substring(firstSpace+1));
            list.add(null);
        } else {
            list.add(string.substring(firstSpace+1, secondSpace));
            list.add(string.substring(secondSpace+1));
        }
        return list;
    }

    public String getDate() {
        return date;
    }

    public int getIntCount() {
        return Integer.parseInt(count);
    }

    public String getCount() {
        return count;
    }

    public String getDifference() {
        return difference;
    }
}