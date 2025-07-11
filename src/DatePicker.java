import org.jdatepicker.impl.JDatePanelImpl;
import org.jdatepicker.impl.JDatePickerImpl;
import org.jdatepicker.impl.UtilDateModel;

import javax.swing.*;
import java.awt.*;
import java.util.Properties;

public class DatePicker {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(DatePicker::createAndShowGUI);
    }

    private static void createAndShowGUI() {
        JFrame frame = new JFrame("Date Picker Example");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(300, 200);

        JPanel panel = new JPanel();
        frame.add(panel);
        placeComponents(panel);

        frame.setVisible(true);
    }

    private static void placeComponents(JPanel panel) {
        panel.setLayout(new FlowLayout());

        UtilDateModel model = new UtilDateModel();
        Properties properties = new Properties();
        JDatePanelImpl datePanel = new JDatePanelImpl(model, properties);
        JDatePickerImpl datePicker = new JDatePickerImpl(datePanel, new DateLabelFormatter());

        panel.add(new JLabel("Select Date:"));
        panel.add(datePicker);
    }
}

class DateLabelFormatter extends JFormattedTextField.AbstractFormatter {
    private final String datePattern = "dd-MM-yyyy";
    private final java.text.SimpleDateFormat dateFormatter = new java.text.SimpleDateFormat(datePattern);

    @Override
    public Object stringToValue(String text) throws java.text.ParseException {
        return dateFormatter.parseObject(text);
    }

    @Override
    public String valueToString(Object value) throws java.text.ParseException {
        if (value != null) {
            java.util.Calendar cal = (java.util.Calendar) value;
            return dateFormatter.format(cal.getTime());
        }

        return "";
    }
}
