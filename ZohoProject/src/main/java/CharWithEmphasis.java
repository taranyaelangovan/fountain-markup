
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.*;
import java.nio.charset.StandardCharsets;
import org.json.simple.JSONObject;
import org.json.simple.JSONArray;

public class CharWithEmphasis {
    
    public static String italicregex="([^*_]*)([*]{1})([^*]{1})(.*)([^*]{1})([*]{1})([^*_]*)";
    public static String boldregex="([^*_]*)([*]{2})([^*]{1})(.*)([^*]{1})([*]{2})([^*_]*)";
    public static String bolditalicregex="([^*_]*)([*]{3})([^*]{1})(.*)([^*]{1})([*]{3})([^*_]*)";
    public static String underlineregex="([^*_]*)([_]{1})([^_]+)([_]{1})([^*_]*)";
    
    
    public static void main (String args[]) throws Exception {
        Scanner sc=new Scanner(System.in);
        System.out.println("Enter file name: ");
        String filename=sc.nextLine();

        String content = "";
            try
            {
                content = new String (Files.readAllBytes(Paths.get(filename)));
            } 
            catch (IOException e) 
            {
                e.printStackTrace();
            }
            
        //System.out.println("File content:\n"+content);
        //System.out.println(content.length());
        //System.out.println(content.indexOf("    "));
        byte[] bytes = content.getBytes(StandardCharsets.UTF_8);
        String charsetstring = new String(bytes, StandardCharsets.UTF_8);
        String outputfilename=filename.substring(0,filename.indexOf("."))+"out.txt";

        try {    
                FileWriter fw=new FileWriter(outputfilename);    
                fw.write(charsetstring);    
                fw.close();    
        }
        catch(Exception e) {
                System.out.println(e);
        } 
        

        //String[] lines = charsetstring.split("\\n");
        //lines.
        
        
       // int flag;
 
        //System.out.println("no of chars: "+charsetstring.length());
        //EMPHASIS CHECKING.
        int i;
        int count=0;
        int c=0;
        String[] lines = charsetstring.split("\\r");
        int m=0;
        //for each line
        for(int k=0;k<lines.length;k++){
            /*for(int j=0;j<lines[k].length();j++){
                c++;
            }*/
            String inp=lines[k];
            String emphasis=CheckRegex(inp);
            String original=inp;
            System.out.println(inp);

            ArrayList<String> splitcontent = new ArrayList<>();
            ArrayList<String> marker = new ArrayList<>();
            
            //JSONArray JSONArr=new JSONArray();


            while(emphasis!="none"){ 

                String empstr=FindString(emphasis,inp);
                //String sep=SplitIndex(emphasis);

                String[] inparr=inp.split(SplitIndex(emphasis));
                ArrayList<String> splitarr = new ArrayList<>();
                for(String a:inparr)
                    splitarr.add(a);

                if(splitarr.get(0).isEmpty()){
                    splitarr.remove(0);
                }

                int nextcheck=-1;
                ArrayList<String> mark = new ArrayList<>();

                for(i=0;i<splitarr.size();i++){
                    if(empstr.contains(splitarr.get(i))){
                        mark.add(EmphasisMarker(emphasis));
                        nextcheck=i;
                    }
                    else{
                        mark.add("n");
                    }
                }
                /*if(arr.length==2){
                    //mark.add("n");
                    if(arr[0].isBlank()){
                               //
                    }
                }*/
                if(inp==original){
                    marker=mark;
                    splitcontent=splitarr;
                    //for(String a:arr)
                        //splitcontent.add(a);
                    //if(arr.length==2)
                        //splitcontent.remove(0);

                }
                else{
                    int n=(marker.size()+1)/2;
                    n=n-1;
                    int insertat=n;
                    for(int j=0;j<marker.size();j++){
                        if(!marker.get(j).contains("n")){
                            insertat=j;
                            break;
                        }
                    }
                    n=insertat;
                    //System.out.println(n+" "+(nextcheck-1)+" "+insertat);

                    for(i=0;i<mark.size();i++){
                        mark.set(i, mark.get(i).concat(marker.get(n)));
                    }
                    //WRITE MARK BACK TO MARKER
                    //replace middle with three
                    marker.set(n, mark.get(0));
                    for(i=1;i<mark.size();i++)
                    {
                        marker.add(n+i,mark.get(i));
                    }
                    splitcontent.set(n, splitarr.get(0));
                    for(i=1;i<splitarr.size();i++)
                    {
                        splitcontent.add(n+i,splitarr.get(i));
                    }
                    //marker.add(index, element);
                }

                /*
                System.out.println("\n");
                for(String a:splitarr)
                    System.out.print(a+" - ");
                System.out.println();
                for(String a:mark)
                    System.out.print(a+" - ");
                */
                inp=splitarr.get(nextcheck);
                emphasis=CheckRegex(inp);
                System.out.println();

            }
            if(splitcontent.size()==0){
                splitcontent.add(inp);
                marker.add("n");
            }
            /*
            System.out.println("\nFINAL:\n");
            for(String a:splitcontent)
                System.out.print(a+" - ")
            System.out.println();
            
            for(String a:marker)
                System.out.print(a+" - ");
            */

            for(i=0;i<marker.size();i++){
                if((marker.get(i).length()>=2)&&(marker.get(i).contains("n"))){
                    String correctedstr=RemoveChar('n',marker.get(i));
                    marker.set(i, correctedstr);
                }
            }

            /*
            System.out.println();
            for(String a:marker)
                System.out.print(a+" - ");
            */

            String finalstr="";
            JSONArray EmphasisArr=new JSONArray();
            
            ///System.out.println();
            for(i=0;i<splitcontent.size();i++){ //i->index of marker and splitcontent
                String emp=marker.get(i);
                //System.out.print("\n"+emp+": ");
                JSONObject obj=new JSONObject();
                String style="";
                for(int l=0;l<emp.length();l++){
                    style=style.concat(FindEmphasis(emp.charAt(l))+" ");
                }
                if(emp!="n"){
                    obj.put("type","text");
                    obj.put("style", style);
                }
                else{
                    obj.put("type","text");
                }
                //j->index of each string in split content
                for(int j=0;j<splitcontent.get(i).length();j++){
                    m++;
                    EmphasisArr.add(obj);
                    //JSONArr.set(j, content);
                }
                finalstr=finalstr.concat(splitcontent.get(i));
            }

            for(i=0;i<finalstr.length();i++){
                if(finalstr.charAt(i)!='\r')
                    System.out.println(finalstr.charAt(i)+"\t"+EmphasisArr.get(i));
            }
            //count=EmphasisArr.size();
        }
        //System.out.println("no: "+m+"   jsonarr length: "+count);
    }
    
    public static String CheckRegex(String str){
        String ret;
        if(Pattern.matches(italicregex,str)) {
            ret="italic";
        }
        else if(Pattern.matches(boldregex,str)){
            ret="bold";
        }
        else if(Pattern.matches(bolditalicregex,str)){
            ret="bold italic";
        }
        else if(Pattern.matches(underlineregex,str)){
            ret="underline";
        }
        else{
            ret="none";
        }
        return ret;
    }


    public static String FindString(String reg,String inp){
        String str="";
        if(reg=="italic"){
            int startindex=inp.indexOf("*");
            int endindex=inp.indexOf("*", startindex+1);
            endindex=endindex+1;
            //System.out.println(startindex+"  "+endindex);
            str=inp.substring(startindex, endindex);
        }
        if(reg=="bold"){
            int startindex=inp.indexOf("**");
            int endindex=inp.indexOf("**", startindex+1);
            endindex=endindex+2;
            //System.out.println(startindex+"  "+endindex);
            str=inp.substring(startindex,endindex);
        }
        if(reg=="bold italic"){
            int startindex=inp.indexOf("***");
            int endindex=inp.indexOf("***", startindex+1);
            endindex=endindex+3;
            //System.out.println(startindex+"  "+endindex);
            str=inp.substring(startindex,endindex);
        }
        if(reg=="underline"){
            int startindex=inp.indexOf("_");
            int endindex=inp.indexOf("_", startindex+1);
            endindex=endindex+1;
            //System.out.println(startindex+"  "+endindex);
            str=inp.substring(startindex, endindex);
        }
        return str;
    }

    public static String SplitIndex(String reg){
        String separator="";
        if(reg=="italic")
            separator="\\*";
        else if(reg=="bold")
            separator="\\*\\*";
        else if(reg=="bold italic")
            separator="\\*\\*\\*";
        else if(reg=="underline")
            separator="_";
        return separator;
    }

    public static String EmphasisMarker(String reg){
        String marker="";
        if(null!=reg)
            switch (reg) {
            case "italic":
                marker="i";
                break;
            case "bold":
                marker="b";
                break;
            case "bold italic":
                marker="t";
                break;
            case "underline":
                marker="u";
                break;
            default:
                break;
        }
        return marker;
    }
    
    public static String RemoveChar(char c,String str){
        String modifiedstr;
        int i=str.indexOf(c);
        modifiedstr=str.substring(0,i)+str.substring(i+1);
        //System.out.println(str+"  "+modifiedstr);
        return modifiedstr;
    }
    
    public static String FindEmphasis(char m){
        String emp="";
        switch(m){
            case 'i':
                emp="italic";
                break;
            case 'b':
                emp="bold";
                break;
            case 't':
                emp="bold italic";
                break;
            case 'u':
                emp="underlined";
                break;
        }
        return emp;
    }
}

