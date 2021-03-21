import javax.swing.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Parameter;

public class ConstructorWrapper {
    private Constructor constructor;

    public ConstructorWrapper(Constructor constructor){
        this.constructor = constructor;
    }

    @Override
    public String toString() {
        StringBuilder listView = new StringBuilder();
        Utility.appendConstructorInfo(listView,constructor);
        return  listView.toString();
    }

    public Object instatiate() throws InstantiationException, IllegalAccessException, InvocationTargetException, ClassNotFoundException {
        Parameter[] parameters = constructor.getParameters();
        Object[] arguments = new Object[parameters.length];
        if(parameters.length != 0){
            for(int i = 0; i != parameters.length; i++){
                Class parameterType = parameters[i].getType();
                if(parameterType.isPrimitive() || parameterType.equals(Class.forName("java.lang.String"))){
                    //If type is primitive or a String, get the input directly
                    String userInput = (String) JOptionPane.showInputDialog(null, "Please, enter " + parameterType.toString() + i,"Input Dialog Box", JOptionPane.QUESTION_MESSAGE);
                    try{
                        arguments[i] = Utility.getWrappedPrimitive(parameterType.getName(), userInput);
                    }catch (NumberFormatException exc){
                        //Couldn't parse primitive type. Try again
                        i--;
                    }
                }else{
                    //Reference types must be constructed, invoke special InputDialogBox
                    ConstructorDialogBox dialog = new ConstructorDialogBox(parameterType);
                    dialog.pack();
                    dialog.setVisible(true);
                    //At this point calling thread waits for the DialogBox to be destroyed
                    arguments[i] = dialog.getObject(); //Get the input from the dialogBox
                }
            }
        }
        return constructor.newInstance(arguments);
    }
}
