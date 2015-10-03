package org.dynami.ta.gen;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.tictactec.ta.lib.CoreAnnotated;
import com.tictactec.ta.lib.meta.annotation.FuncInfo;
import com.tictactec.ta.lib.meta.annotation.InputParameterInfo;
import com.tictactec.ta.lib.meta.annotation.IntegerList;
import com.tictactec.ta.lib.meta.annotation.IntegerRange;
import com.tictactec.ta.lib.meta.annotation.OptInputParameterInfo;
import com.tictactec.ta.lib.meta.annotation.OptInputParameterType;
import com.tictactec.ta.lib.meta.annotation.OutputParameterInfo;
import com.tictactec.ta.lib.meta.annotation.OutputParameterType;
import com.tictactec.ta.lib.meta.annotation.RealRange;

public class TAGeneratorFactory {
	public final class InputFlags {
	    static public final double OPEN         = 1;
	    static public final double HIGH         = 2;
	    static public final double LOW          = 4;
	    static public final double CLOSE        = 8;
	    static public final double VOLUME       = 16;
	    static public final double OPENINTEREST = 32;
	    static public final double TIMESTAMP    = 64;
	}
	
	public final class FunctionFlags {
		static public final int OVERLAP = 16777216;
		static public final int VOLUME = 67108864;
		static public final int UNSTABLE_PERIOD = 134217728;
		static public final int PATTERN = 268435456;
	}
	
	public final static String PKG = "org.dynami.ta.";
	public final static String PATH = "./src";
	public static void main(String[] args){
		try {
			new TAGeneratorFactory().generateAll();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private Map<String, String> pkg_ext = new HashMap<String, String>();
	public TAGeneratorFactory() {
		pkg_ext.put("Overlap Studies", "overlap_studies");
		pkg_ext.put("Math Transform", "math_transform");
		pkg_ext.put("Math Operators", "math");
		pkg_ext.put("Volume Indicators", "volume");
		pkg_ext.put("Momentum Indicators", "momentum");
		pkg_ext.put("Volatility Indicators", "volatility");
		pkg_ext.put("Price Transform", "price_transform");
		pkg_ext.put("Statistic Functions", "stat");
		pkg_ext.put("Pattern Recognition", "pattern");
		pkg_ext.put("Cycle Indicators", "cycle");
	}
	
	public void generateAll() throws Exception {
		Method[] methods = CoreAnnotated.class.getDeclaredMethods();
		FuncInfo info;
		for (int i = 0; i < methods.length; i++) {
			info = methods[i].getAnnotation(FuncInfo.class);
			if(info != null){
				generate(methods[i], info);
			}
		}
	}
	
	private void generate(Method method, FuncInfo info) throws Exception{
		boolean needsMATypeImport = false;
		String _methodName = method.getName();
		String _package = PKG+pkg_ext.get(info.group());
		
		//String _name = info.name();
		String _className = getHumanNameFromMethod(_methodName, false);
		List<String> inSeriesNames = new ArrayList<String>();
		List<String> inParamNames = new ArrayList<String>();
		List<String> inIntegerParamNames = new ArrayList<String>();
		List<String> inParamTypes = new ArrayList<String>();
		Map<String, String> inParamDefValues = new HashMap<String, String>();
		
		List<String> outParamNames = new ArrayList<String>();
		List<String> outParamTypes = new ArrayList<String>();
		
		final String fullPath = generatePackageDirectoryTree(PATH, _package);
		
		Annotation[][] paramAnnots = method.getParameterAnnotations();
		Class<?>[] paramTypes = method.getParameterTypes();
		
		// fetch params, skip first two params
		for(int i = 2; i < paramTypes.length; i++){
			// fetch param annotations
			for(int j = 0; j < paramAnnots[i].length; j++){
				if(paramAnnots[i][j] instanceof InputParameterInfo){
					int flags = ((InputParameterInfo)paramAnnots[i][j]).flags();
					inSeriesNames.addAll(getInputParamNames(((InputParameterInfo)paramAnnots[i][j]).paramName(), flags));
				}
				if(paramAnnots[i][j] instanceof OptInputParameterInfo){
					String inParamName =camelize(((OptInputParameterInfo)paramAnnots[i][j]).displayName()); 
					inParamNames.add(inParamName);
					if(OptInputParameterType.TA_OptInput_IntegerRange.equals(((OptInputParameterInfo)paramAnnots[i][j]).type())){
						inParamTypes.add("int");
						inParamDefValues.put(inParamName, String.valueOf(((IntegerRange)paramAnnots[i][j+1]).defaultValue()));
						inIntegerParamNames.add(inParamName);
					} else if(OptInputParameterType.TA_OptInput_IntegerList.equals(((OptInputParameterInfo)paramAnnots[i][j]).type())){
						inParamTypes.add("MAType");
						inParamDefValues.put(inParamName,  "MAType."+properCase(((IntegerList)paramAnnots[i][j+1]).string()[((IntegerList)paramAnnots[i][j+1]).defaultValue()]));
						needsMATypeImport = true;
					} else if(OptInputParameterType.TA_OptInput_RealRange.equals(((OptInputParameterInfo)paramAnnots[i][j]).type())){
						inParamTypes.add("double");
						inParamDefValues.put(inParamName, String.valueOf(((RealRange)paramAnnots[i][j+1]).defaultValue()));
					} else if(OptInputParameterType.TA_OptInput_RealList.equals(((OptInputParameterInfo)paramAnnots[i][j]).type())){
						inParamTypes.add("double[]");
					}
				}
				
				if(paramAnnots[i][j] instanceof OutputParameterInfo){
					outParamNames.add(camelize(((OutputParameterInfo)paramAnnots[i][j]).paramName()));
					if(OutputParameterType.TA_Output_Real.equals(((OutputParameterInfo)paramAnnots[i][j]).type())){
						outParamTypes.add("double[]");
					} else if(OutputParameterType.TA_Output_Integer.equals(((OutputParameterInfo)paramAnnots[i][j]).type())){
						outParamTypes.add("int[]");
					}
				}
			}
		}
		
		//String _annots = getIndicatorAnnots(_methodName, info.flags(), outParamNames);
		
		BufferedWriter w = null;
		try {
            //Construct the BufferedWriter object
            w = new BufferedWriter(new FileWriter(fullPath+File.separator+_className+".java"));
            
            //LICENCE
            w.write("/*\n");
            w.write(" * Copyright 2015 Alessandro Atria - a.atria@gmail.com\n");
            w.write(" *\n");
            w.write(" * Licensed under the Apache License, Version 2.0 (the \"License\");\n");
            w.write(" * you may not use this file except in compliance with the License.\n");
            w.write(" * You may obtain a copy of the License at\n");
            w.write(" *\n");
            w.write(" *        http://www.apache.org/licenses/LICENSE-2.0\n");
            w.write(" *\n");
            w.write(" * Unless required by applicable law or agreed to in writing, software\n");
            w.write(" * distributed under the License is distributed on an \"AS IS\" BASIS,\n");
            w.write(" * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\n");
            w.write(" * See the License for the specific language governing permissions and\n");
            w.write(" * limitations under the License.\n");
            w.write(" */\n");
            
            
            w.write("package "+_package+";\n\n");
            w.write("import com.tictactec.ta.lib.MInteger;\n");
//            w.write("import org.dynami.core.chart.Plot;\n");
            w.write("import java.util.Arrays;\n");
            w.write("import java.util.List;\n");
            w.write("import java.util.function.Supplier;\n");
            w.write("import org.dynami.ta.TaLibIndicator;\n");
            w.write("import org.dynami.core.data.Series;\n");
            w.write("import org.dynami.core.utils.DUtils;\n");
            
//            if(_annots != null){
//            	if(_annots.startsWith("@Overlap")){
//            		w.write("import org.dynami.core.ta.annots.Overlap;\n");
//            	} else {
//            		w.write("import org.dynami.core.ta.annots.Pattern;\n");
//            	}
//            }
            if(needsMATypeImport){
            	w.write("import com.tictactec.ta.lib.MAType;\n");
            }
            //w.write("import org.dynami.core.ta.*;\n\n");
            w.write("/**\n");
            w.write(" * GENERATED CODE\n");
            w.write(" */\n");
//            if(_annots != null){
//            	w.write(_annots+"\n");
//            }
            w.write("public class "+_className+" extends TaLibIndicator {\n");
            
            for(int i = 0 ; i < inParamNames.size(); i++){
            	w.write("\tprivate "+inParamTypes.get(i)+" ");
        		w.write(inParamNames.get(i)+" = ");
        		w.write(inParamDefValues.get(inParamNames.get(i))+";\n");
        	}
//            w.write("\tprivate int lastLength = 0;\n");
//            w.write("\tprivate int PAD = 4;\n");
//            w.write("\tprivate boolean ready = false;\n");
            w.write("\t// output series\n");
    		for(int i = 0 ; i < outParamNames.size(); i++){
    			w.write("\tprivate Series ");
        		w.write(outParamNames.get(i)+" = new Series();\n");
    		}
            
            w.write("\t\n");
            if(inParamNames.size() > 0){
	            // costruttore senza parametri solo ci sono parametri in input
	            w.write("\t/**\n");
	            w.write("\t * Default constructor with standard input parameters</br>\n");
	            for(int i = 0 ; i < inParamNames.size(); i++){
	            	w.write("\t * default value for ");
	        		w.write(inParamNames.get(i)+" = ");
	        		w.write(inParamDefValues.get(inParamNames.get(i))+"</br>\n");
	        	}
	            w.write("\t */\n");
	            w.write("\tpublic "+_className+"(){\n");
	            w.write("\t\tcomputePad(");
	            for(String s:inIntegerParamNames){
	            	w.write(" "+s+", ");
	            }
	            w.write("PAD);\n");
	            w.write("\t}\n\n");
            }
            w.write("\t/**\n");
            w.write("\t * Default constructor with custom input parameters:\n");
            for(int i = 0 ; i < inParamNames.size(); i++){
            	w.write("\t * @param ");
        		w.write(inParamNames.get(i)+" default value ");
        		w.write(inParamDefValues.get(inParamNames.get(i))+"\n");
        	}
            w.write("\t */\n");
            w.write("\tpublic "+_className+"(");
            for(int i = 0 ; i < inParamNames.size(); i++){
            	if(i > 0)w.write(", ");
            	w.write(inParamTypes.get(i)+" ");
        		w.write(inParamNames.get(i));
        	}
            w.write("){\n");
            for(int i = 0 ; i < inParamNames.size(); i++){
            	w.write("\t\tthis."+inParamNames.get(i)+" = "+inParamNames.get(i)+";\n");
        	}
            w.write("\t\tcomputePad(");
            for(String s:inIntegerParamNames){
            	w.write(" "+s+", ");
            }
            w.write("PAD);\n");
            w.write("\t}\n\n");
            
            w.write("\tprivate void computePad(int...v){\n");
            w.write("\t\tint max = Integer.MIN_VALUE;\n");
            w.write("\t\tfor(int d : v){\n");
            w.write("\t\t\tif(d > max)\n");
            w.write("\t\t\t\tmax = d;\n");
            w.write("\t\t}\n");
            w.write("\t\tPAD = max;\n");
            w.write("\t}\n\n");
            
            w.write("\t@Override\n");
            w.write("\tpublic String getName(){\n");
        	w.write("\t\treturn \""+getHumanNameFromMethod(_methodName, true)+ "\";\n");
        	w.write("\t}\n\n");
            
        	w.write("\t@Override\n");
            w.write("\tpublic String getDescription(){\n");
        	w.write("\t\treturn \""+getHumanNameFromMethod(_methodName, true)+" - "+info.group()+"\";\n");
        	w.write("\t}\n\n");
        	
        	
        	w.write("\t/**\n");
            w.write("\t * Compute indicator based on constructor class parameters \n");
            w.write("\t * and input Series.\n");
            w.write("\t */\n");
        	// definizione del metodo compute()
        	w.write("\tpublic void compute( ");
        	for(int i = 0 ; i < inSeriesNames.size(); i++){
        		if(i > 0) w.write(", ");
        		w.write("final Series "+inSeriesNames.get(i));
        	}
        	w.write(") {\n");
        	w.write("\t\tfinal MInteger outBegIdx = new MInteger();\n");
        	w.write("\t\tfinal MInteger outNBElement = new MInteger();\n");
        	
        	w.write("\t\t// define strict necessary input parameters\n");
        	w.write("\t\tfinal int currentLength = "+inSeriesNames.get(0)+".size();\n");
        	for(int i = 0 ; i < inSeriesNames.size(); i++){
        		w.write("\t\tfinal double[] _tmp"+inSeriesNames.get(i)+" = "+inSeriesNames.get(i)+".toArray(Math.max(lastLength-PAD, 0), currentLength);\n");
        	}
        	
        	w.write("\t\t// define output parameters\n");
        	for(int i = 0 ; i < outParamNames.size(); i++){
        		w.write("\t\tfinal "+outParamTypes.get(i)+" _"+outParamNames.get(i)+" = new "+(outParamTypes.get(i).substring(0, outParamTypes.get(i).length()-2))+"[_tmp"+inSeriesNames.get(0)+".length];\n");
        	}
        	w.write("\t\t\n");
        	
        	w.write("\t\tready = DUtils.max(");
        	for(int i = 0 ; i < inSeriesNames.size(); i++){
        		if(i > 0) {
        			w.write(", ");
        		}
        		w.write("_tmp"+inSeriesNames.get(i)+".length");
        	}
        	w.write(") >= DUtils.max(");
        	for(int i = 0, j = 0 ; i < inParamNames.size(); i++){
        		if(inParamTypes.get(i).equals("int")){
        			if(j > 0) {
        				w.write(", ");
        			}
        			j++;
        			w.write(inParamNames.get(i));
        		}
        	}
        	w.write(");\n");
        	
        	// invocazione del metodo core 
        	w.write("\t\t// calculate output\n");
        	w.write("\t\tcore."+_methodName+"(0, _tmp"+inSeriesNames.get(0)+".length-1, ");
        	for(int i = 0 ; i < inSeriesNames.size(); i++){
        		w.write("_tmp"+inSeriesNames.get(i)+", ");
        	}
        	
        	for(int i = 0 ; i < inParamNames.size(); i++){
        		w.write("this."+inParamNames.get(i)+", ");
        	}
        	w.write("outBegIdx, outNBElement, ");
        	for(int i = 0 ; i < outParamNames.size(); i++){
        		w.write("_"+outParamNames.get(i));
        		if(i < outParamNames.size()-1){
        			w.write(", ");
        		}
        	}
        	w.write(");\n ");
        	
        	w.write("\t\t// shift data to end of array and set output fields\n");
        	for(int i = 0 ; i < outParamNames.size(); i++){
        		w.write("\t\tDUtils.shift(_"+outParamNames.get(i)+", outBegIdx.value);\n");
        	}
        	
        	w.write("\t\tfor(int i = lastLength, j = currentLength-lastLength; i < currentLength; i++, lastLength++, j--){\n");
        	for(int i = 0 ; i < outParamNames.size(); i++){
        		w.write("\t\t\t"+outParamNames.get(i)+".append(_"+outParamNames.get(i)+"[_"+outParamNames.get(i)+".length-j]);\n");
        	}
        	w.write("\t\t}\n");
        	
        	w.write("\t}\n\n");
        	
        	if(outParamNames.size() > 1){
        		for(int i = 0 ; i < outParamNames.size(); i++){
        			//w.write("\t@Plot(name=\""+outParamNames.get(i).substring(3)+"\")\n");
	        		w.write("\tpublic Series get"+outParamNames.get(i).substring(3)+"(){\n");
	        		w.write("\t\treturn "+outParamNames.get(i)+";\n");
	        		w.write("\t}\n");
        		}
        	} else {
        		//w.write("\t@Plot(name=\""+getHumanNameFromMethod(_methodName, true)+"\")\n");
        		w.write("\tpublic Series get(){\n");
        		w.write("\t\treturn "+outParamNames.get(0)+";\n");
        		w.write("\t}\n");
        	}
        	w.write("\n");
        	w.write("\t@Override\n");
        	w.write("\tpublic List<Supplier<Series>> series() {\n");
        	w.write("\t\treturn Arrays.asList(");
        	if(outParamNames.size() > 1){
        		for(int i = 0 ; i < outParamNames.size(); i++){
        			if(i > 0)
        				w.write(", ");
        			
        			w.write("this::get"+outParamNames.get(i).substring(3));
        		}
        	} else {
        		w.write("this::get");
        	}
        	
        	w.write(");\n");
        	w.write("\t}\n");
        	
        	w.write("\t@Override\n");
        	w.write("\tpublic boolean isReady() {\n");
        	w.write("\t\treturn ready;\n");
        	w.write("\t}\n");
        	
        	w.write("\t@Override\n");
        	w.write("\tpublic String[] seriesNames() {\n");
        	w.write("\t\treturn new String[]{");
        	if(outParamNames.size()>1){
	    		for(int i = 0 ; i < outParamNames.size(); i++){
	    			if(i > 0)
	    				w.write(", ");
	    			w.write("\"");
	    			w.write(outParamNames.get(i).substring(3));
	    			w.write("\"");
	    		}
        	} else {
        		w.write("\"");
        		w.write(getHumanNameFromMethod(_methodName, true));
        		w.write("\"");
        	}
        	w.write("};\n");
        	w.write("\t}\n");
        	
            w.write("}\n\n");
            
		} catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            //Close the BufferedWriter
            try {
                if (w != null) {
                    w.flush();
                    w.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
	}
	
	private String getHumanNameFromMethod(String name, boolean withSpace){
		char[] _name = name.toCharArray();
		StringBuilder buffer = new StringBuilder();
		for (int i = 0; i < _name.length; i++) {
			if(i == 0){
				buffer.append(Character.toUpperCase(_name[i]));
				continue;
			} 
			if(Character.isUpperCase(_name[i])){
				if(withSpace){
					buffer.append(" ");
				}
				buffer.append(_name[i]);
			} else {
				buffer.append(_name[i]);
			}
		}
		return buffer.toString();
	}
	
	public static String camelize(String name){
		char[] _name = name.toCharArray();
		StringBuilder buffer = new StringBuilder();
		for (int i = 0; i < _name.length; i++) {
			if(i == 0){
				buffer.append(Character.toLowerCase(_name[i]));
				continue;
			} 
			if(Character.isSpaceChar(_name[i])){
				buffer.append(Character.toUpperCase(_name[++i]));
			} else {
				buffer.append(_name[i]);
			}
		}
		return buffer.toString().replace('-', '_');
	}
	
	private String generatePackageDirectoryTree(String sourcePath, String _package) throws Exception{
		String fullPath = sourcePath+File.separatorChar+_package.replace('.', File.separatorChar);
		System.out.println(fullPath);
		File dir = new File(fullPath);
		dir.mkdirs();
		return fullPath;
	}
	
	private String properCase(String in){
		StringBuilder buffer = new StringBuilder();
		char[] _in = in.toCharArray();
		for (int i = 0; i < _in.length; i++) {
			if(i == 0){
				buffer.append(Character.toUpperCase( _in[i] ));
			} else {
				buffer.append(Character.toLowerCase( _in[i] ));
			}
		}
		return buffer.toString();
	}
	
	private List<String> getInputParamNames(final String defName, final int flag){
		ArrayList<String> output = new ArrayList<String>();
		if(flag == 0){
			output.add(defName);
			return output;
		}
		int input = flag;
		
		if(flag/InputFlags.TIMESTAMP >= 1){
			output.add("timeStamp");
			input -= InputFlags.TIMESTAMP;
		}
		if(input/InputFlags.OPENINTEREST >= 1){
			output.add("openInterest");
			input -= InputFlags.OPENINTEREST;
		}
		if(input/InputFlags.VOLUME >= 1){
			output.add("volume");
			input -= InputFlags.VOLUME;
		}
		if(input/InputFlags.CLOSE >= 1){
			output.add("close");
			input -= InputFlags.CLOSE;
		}
		if(input/InputFlags.LOW >= 1){
			output.add("low");
			input -= InputFlags.LOW;
		}
		if(input/InputFlags.HIGH >= 1){
			output.add("high");
			input -= InputFlags.HIGH;
		}
		if(input/InputFlags.OPEN >= 1){
			output.add("open");
			input -= InputFlags.OPEN;
		}
		
		output.trimToSize();
		reverse(output);
		return output;
	}
	
	private String getIndicatorAnnots(final String defName, final int flag, final List<String> outParamNames){
		ArrayList<String> output = new ArrayList<String>();
		if(flag == 0){
			StringBuilder buffer = new StringBuilder("@Overlap");
			if(outParamNames.size() > 1){
				buffer.append("(out={");
				for(int i = 0; i < outParamNames.size(); i++){
					if( i > 0)buffer.append(",");
					buffer.append("\""+outParamNames.get(i)+"\"");
				}
				buffer.append("})");
			}
			
			return buffer.toString();
		}
		int input = flag;
		
		if(flag/FunctionFlags.PATTERN >= 1){
			//output.add("Overlap.ON.PARENT");
			input -= FunctionFlags.PATTERN;
			return "@Pattern";
		}
		if(input/FunctionFlags.UNSTABLE_PERIOD >= 1){
			//output.add("UNSTABLE");
			input -= FunctionFlags.UNSTABLE_PERIOD;
		}
		if(input/FunctionFlags.VOLUME >= 1){
			output.add("Overlap.ON.VOLUME");
			input -= FunctionFlags.VOLUME;
		}
		if(input/FunctionFlags.OVERLAP >= 1){
			output.add("Overlap.ON.PRICE");
			input -= FunctionFlags.OVERLAP;
		}
		
		return null;
	}
	
	public static <T> void reverse(final List<T> a){
    	T temp = null;
    	int length = a.size();
    	for (int i = 0; i < length/2; i++) {
    		temp = a.get(i);
    		a.set(i,  a.get(length-i-1));
    		a.set(length-i-1, temp);
		}
    }
}
