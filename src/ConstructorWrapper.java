import javax.swing.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.Parameter;

public class ConstructorWrapper {
    private Constructor constructor;

    public ConstructorWrapper(Constructor constructor){
        this.constructor = constructor;
    }

    @Override
    public String toString() {
        return constructor.toString();
    }

    public Object instatiate() {
        Parameter[] parameters = constructor.getParameters();
        Object[] arguments = new Object[parameters.length];
        try{
            if(parameters.length>0){
                for(int i = 0; i != parameters.length; i++){
                    String response = (String) JOptionPane.showInputDialog(null, parameters[i].getType().toString(),"Provide input",JOptionPane.QUESTION_MESSAGE);
                    if(parameters[i].getType().isPrimitive()){
                        //I have to convert it
                    }else{
                        //I have to recursively build another object first
                    }
                    System.out.println(response);
                }
            }else{
                System.out.println("CREATED NEW OBJECT");
                return constructor.newInstance(null);
            }
        }catch (Exception e){
            System.out.println("Couldn't create an instance");
        }
        System.out.println("CREATED NEW OBJECT");
        return null;
    }
}
