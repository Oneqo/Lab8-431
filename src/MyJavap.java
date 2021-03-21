import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.net.URL;
import java.net.URLClassLoader;

public class MyJavap {
    private JTextField fileChooserTextField;
    private JButton instantiateButton;
    private JButton executeButton;
    private JPanel mainPanel;
    private JTextArea outputs;
    private JList methodList;
    private JList constructorList;

    private Object objectUnderTesting;

    public MyJavap() {
        //Main JFrame
        JFrame frame = new JFrame();
        frame.add(mainPanel);
        frame.setSize(550,500);
        frame.setMinimumSize(new Dimension(550,500));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

        //Selector textbox
        fileChooserTextField.setEditable(true);
        fileChooserTextField.setEnabled(false);

        //Outputs
        outputs.setEditable(false);

        //Action listeners
        fileChooserTextField.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                JFileChooser fileChooser = new JFileChooser();
                FileNameExtensionFilter filter = new FileNameExtensionFilter("Class file filter", "class");
                fileChooser.setFileFilter(filter);
                int returnValue = fileChooser.showOpenDialog(mainPanel);
                if(returnValue == JFileChooser.APPROVE_OPTION){
                    fileChooserTextField.setText(fileChooser.getSelectedFile().getPath());
                    Class c = getClassFromPath(fileChooser.getSelectedFile());
                    populateConstructorList(c);
                    populateMethodList(c);
                }
            }
        });
        //Instantiates an object
        instantiateButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                if(!constructorList.isSelectionEmpty()){
                    ConstructorWrapper constructor = (ConstructorWrapper) constructorList.getSelectedValue();
                    try{
                        objectUnderTesting = constructor.instatiate();
                    }catch (Exception er){
                        //If object creation fails for some reason, set the object reference to null.
                        System.err.println(er);
                        objectUnderTesting = null;
                    }
                }
            }
        });
        //Runs methods
        executeButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                //TODO: Mikayla, place your code for invoking a selected method in here
                //You may refer to objectUnderTesting variable as the source object. (Make sure it's not null before using tho)

                if(objectUnderTesting == null || methodList.getSelectedValue() == null)
                {
                    //nothing has been selected. do nothing.
                    return;
                }

                Method method = (Method) methodList.getSelectedValue();
                Parameter[] parameters = new Parameter[method.getParameterCount()];
                parameters = method.getParameters();
                Object[] arguments = new Object[parameters.length];
                //get user input for arguments if necessary
                if(parameters.length != 0)
                {
                    for(int i = 0; i < parameters.length; i++)
                    {
                        Class parameterType = parameters[i].getType();
                        try {
                            if (parameterType.isPrimitive() || parameterType.equals(Class.forName("java.lang.String")))
                            {
                                String userInput = (String) JOptionPane.showInputDialog(null, "Please, enter " + parameterType.toString() + i,"Input Dialog Box", JOptionPane.QUESTION_MESSAGE);
                                try{
                                    arguments[i] = Utility.getWrappedPrimitive(parameterType.getName(), userInput);
                                }catch (NumberFormatException exc){
                                    //Couldn't parse primitive type. Try again
                                    i--;
                                }
                            }
                            else{
                                ConstructorDialogBox dialogBox = new ConstructorDialogBox(parameterType);
                                dialogBox.pack();
                                dialogBox.setVisible(true);
                            }
                        }
                        catch(ClassNotFoundException exc)
                        {
                            exc.printStackTrace();
                        }
                    }
                }

                try
                {
                    if(objectUnderTesting == null)
                    {
                        System.out.println("NULL OBJECT");
                    }
                    else if(method == null)
                    {
                        System.out.println("NULL METHOD");
                    }
                    else if(parameters == null)
                    {
                        System.out.println("NULL PARAMS");
                    }
                    Object o = method.invoke(objectUnderTesting, arguments);
                    //illegal access exceptions, but for now it's buggy

                    //an alternative option?
                    //Object o = method.invoke(objectUnderTesting, parameterList);
                    //outputs.setText(o.toString); ???? possibly to view the output
                    if(method.getAnnotatedReturnType().toString().equals("void"))
                    {
                        outputs.append("Void function completed successfully.\n");
                    }
                    else
                    {
                        outputs.append(o.toString() + "\n");
                    }

                }
                catch (Exception exc)
                {
                    outputs.append("You do not have permission to access this.\n");
                    exc.printStackTrace();
                }
            }
        });
    }

    private Class getClassFromPath(File selectedFile) {
        //Set variables
        Class c = null;
        boolean isDone = false;
        //Get file name
        StringBuilder name = new StringBuilder();
        String fileName = selectedFile.getName();
        name.append(fileName);
        name.delete(fileName.length()-6, fileName.length());
        //Get URL to the directory
        File parent = selectedFile.getParentFile();
        URL[] classDirectoryURLs = new URL[1];
        do{
            try{
                classDirectoryURLs[0] = parent.toURI().toURL();
                URLClassLoader loader = new URLClassLoader(classDirectoryURLs);
                //Try to get a class object indicated by className
                c = Class.forName(name.toString(), true, loader);
                isDone = true;
            }catch (NoClassDefFoundError err){
                //Happens if URL is not valid, so we update the name, URL and try again
                if(parent.getParentFile()!=null){
                    name.insert(0,'.');
                    name.insert(0,parent.getName());
                    parent = parent.getParentFile();
                }else{
                    isDone = true;
                }
            }catch (Exception exc){
                System.err.println(exc);
            }
        }while(!isDone);
        return c;
    }

    //This function populates JList that shows constructors
    private void populateConstructorList(Class c){
        Object[] constructors = c.getConstructors();
        Object[] wrappedConstructors = new ConstructorWrapper[constructors.length];
        //Wrap Constructor objects before placing them into JList
        for(int i = 0; i != constructors.length; i++){
            wrappedConstructors[i] = new ConstructorWrapper((Constructor)constructors[i]);
        }
        constructorList.setListData(wrappedConstructors);
    }
    //This function populates JList that shows methods
    private void populateMethodList(Class c){
        //TODO: This is placeholder code. Feel free to change it
        Object[] methods = c.getDeclaredMethods();
        methodList.setListData(methods);
    }

    //Entry-point
    public static void main(String[] args) {
        MyJavap app = new MyJavap();
    }
}
