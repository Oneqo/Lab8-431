import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.lang.reflect.Constructor;

public class ConstructorDialogBox extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JList constructorList;
    private Object instance;

    public ConstructorDialogBox() {
        contentPane.setMinimumSize(new Dimension(300,300));
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        this.instance = null;

        buttonOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        });
    }
    public ConstructorDialogBox(Class classType){
        this();
        Constructor[] constructors = classType.getConstructors();
        Object[] list = new Object[constructors.length];
        for (int i = 0; i!= constructors.length; i++){
            list[i] = new ConstructorWrapper(constructors[i]);
        }
        constructorList.setListData(list);
    }

    private void onOK() {
        if(!constructorList.isSelectionEmpty()){
            ConstructorWrapper constructor = (ConstructorWrapper) constructorList.getSelectedValue();
            try{
                instance = constructor.instatiate();
            }catch (Exception er){
                System.err.println(er);
            }
            dispose();
        }
    }
    public Object getObject(){
        return instance;
    }
}
