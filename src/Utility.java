import java.lang.reflect.*;

public class Utility {
    public static Object getWrappedPrimitive(String type, String value) throws NumberFormatException{
        switch (type){
            case "byte": return Byte.parseByte(value);
            case "short": return Short.parseShort(value);
            case "int": return Integer.parseInt(value);
            case "long": return Long.parseLong(value);
            case "float": return Float.parseFloat(value);
            case "double": return Double.parseDouble(value);
            case "char": return Character.valueOf(value.charAt(0));
            case "boolean": return Boolean.parseBoolean(value);
            case "java.lang.String": return value;
            default: throw new IllegalArgumentException("\'"+type + "\' is not a primitive type.");
        }
    }

    public static void appendConstructorInfo(StringBuilder skeleton, Constructor constructor) {
        if(constructor != null){
            String constructorName = constructor.getName();
            String className = constructorName.substring(constructorName.lastIndexOf('.')+1, constructorName.length());
            skeleton.append(getModifiers(constructor));
            skeleton.append(className);
            skeleton.append(getParameterList(constructor));
            skeleton.append(getExceptionList(constructor));
            skeleton.append(";");
        }
    }
    public static void appendMethodInfo(StringBuilder skeleton, Method method) {
        if (method != null) {
            skeleton.append(getModifiers(method));
            skeleton.append(method.getAnnotatedReturnType() + " ");
            skeleton.append(method.getName());
            skeleton.append(getParameterList(method));
            skeleton.append(getExceptionList(method));
            skeleton.append(";");
        }
    }
    public static String getExceptionList(Executable obj) {
        StringBuilder result = new StringBuilder();
        Class[] exceptions = obj.getExceptionTypes();
        if(exceptions.length>0){
            result.append(" throws ");
            for(Class e : exceptions){
                result.append(e.getName());
                result.append(',');
            }
            result.deleteCharAt(result.length()-1);
        }
        return result.toString();
    }
    public static String getModifiers(Member obj){
        StringBuilder result = new StringBuilder();
        int modifiers = obj.getModifiers();
        result.append(Modifier.toString(modifiers));
        if(!result.toString().isEmpty()){
            result.append(' ');
        }
        return result.toString();
    }
    public static String getParameterList(Executable obj){
        StringBuilder result = new StringBuilder();
        Class[] parameters = obj.getParameterTypes();
        result.append('(');
        if(parameters.length>0){
            for(int i = 0; i!=parameters.length; i++){
                String parameterName = parameters[i].getName();
                String className = parameterName.substring(parameterName.lastIndexOf('.')+1, parameterName.length());
                result.append(className+ " ");
                result.append(className.charAt(0)+""+(i+1));
                result.append(',');
            }
            result.deleteCharAt(result.length()-1);//remove last coma
        }
        result.append(")");
        return result.toString();
    }
}
