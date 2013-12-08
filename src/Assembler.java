/* Daniel Moody
 * Lab 4 Part 1: Assembler
 * Computer Architecture 
 * Start Date: 10-19-2011
 */
import java.io.*;



public class Assembler {
	
/**//////////////////////////////////////////////////////////////////////////////////////**/
/**/		//Adjustments to size of input and file locations							/**/
/**/public static final int NumOfInstructions = 255;									/**/
/**/																					/**/
/**/public static final String InputFileName = "test.txt";								/**/
/**/public static final String OutputFileName = "reconfv.coe";							/**/
/**//////////////////////////////////////////////////////////////////////////////////////**/
	public static final int NumOfInstructions2 = NumOfInstructions + 2; 
	public static location[] location = new location[NumOfInstructions2];
	
	public static String[] Input = new String[NumOfInstructions2];
	public static String[] FormattedInstructions = new String[NumOfInstructions2];
	
	public static int NumOfLocations;
	
	
	public static void main(String[] args)
	{
		
		Input = ReadInstructions(Input);
		
		Input = SpaceFormatter(Input);
		
		Input = NopRemover(Input);
		
		Input = NopInserter(Input);
		
		Input = NumberInserter(Input);
		
		Input = LocationRecorder(Input);
		
		Input = ConvertToMachineCode(Input);
		
		Write(Input);
	}
	
	
	public static String[] ReadInstructions(String[] Input)
	{
		
		// Open the file that is the first 
		// command line parameter
		try {
			FileInputStream fstream = new FileInputStream(InputFileName);
			Input = Read(fstream);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return Input;
	}
	
	// Read Function 
	public static String[] Read(FileInputStream fstream) {
		
		String[] Input = new String[NumOfInstructions2];
		int i = 0;
		
		try{
			// Get the object of DataInputStream
			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String strLine;
		  
			//Read File Line By Line
			while ((strLine = br.readLine()) != null)   {
				// If not null insert it into the String array
				Input[i] = strLine;
				i++;
				
			}
		  
			//Close the input stream
			in.close();
		    }catch (Exception e){//Catch exception if any
		    	System.err.println("Error: " + e.getMessage());
		  }
		
		return Input;
	}
	
	// Output to a file
	public static void Write(String[] out) {

		String FirstLine = "MEMORY_INITIALIZATION_RADIX=2;";
		String SecondLine = "MEMORY_INITIALIZATION_VECTOR=";
		
		FileOutputStream fstream;
		try {
			fstream = new FileOutputStream(OutputFileName);
			new PrintStream(fstream).println (FirstLine);
			new PrintStream(fstream).println (SecondLine);
			for(int i = 0; i < NumOfInstructions2; i++)
			{
				if(out[i] != null)
					new PrintStream(fstream).println(out[i] + ",");
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		    
	}
	
	// Removing all the nops 
	public static String[] NopRemover(String[] Input)
	{
		// Create temp String Array for removing the nops
		String[] Temp = new String[NumOfInstructions2];
		int k = 0;
		int i = 0;
		boolean NOPfound;
		while(Input[i] != null)
		{
			String[] token  = Input[i].split(" ");
			NOPfound = false;
			// Checking the nop if so keep track
			for(int j = 0; j < token.length; j++)
			{
				if(token[j].equals("nop"))
				{
					NOPfound = true;
				}
			}
			
			if(NOPfound == false)
			{
				Temp[k] = Input[i];
				k++;
			}
			i++;
			
		}
		
		return Temp;
	}
	
	//Looks for Data hazards then determines if Nops need to be inserted
	public static String[] NopInserter(String[] Input)
	{
		// Create temp String Array for removing the nops
		String[] Temp = new String[NumOfInstructions2];
		int k = 0;
		int i = 0;
		boolean BranchFound;
		boolean LWHazardFound;
		while(Input[i] != null)
		{
			BranchFound = false;
			LWHazardFound = false;
			String[] token  = Input[i].split(" ");
			
			// Checking the nop if so keep track
			for(int j = 0; j < token.length; j++)
			{
				if(token[j].equals("beq") || token[j].equals("bne") || token[j].equals("jump"))
				{
					BranchFound = true;
					break;
				}
				else if(token[j].equals("lw"))
				{
					if(i < NumOfInstructions2 - 1 && Input[i+1] != null)
					{
						String[] token2  = Input[i+1].split(" ");
						for(int h = 0; h < token2.length; h++)
						{
							if(token2[h].equals("sw") || token2[h].equals("beq") || token2[h].equals("bne"))
							{
								if(token[j+1].equals(token2[h+1]) || token[j+1].equals(token2[h+2]))
								{
									LWHazardFound = true;
									break;
								}
								
							}
							else if(token2[h].equals("add") || token2[h].equals("sub") || token2[h].equals("slt") || 
									token2[h].equals("or") || token2[h].equals("and"))
							{
								if(token[j+1].equals(token2[h+2]) || token[j+1].equals(token2[h+3]))
								{
									LWHazardFound = true;
									break;
								}
							}
							else if(token2[h].equals("lw") || token2[h].equals("lui") || token2[h].equals("jump") || token2[h].equals("nop"))
							{
								break;
							}
						}
						
					}
					break;
				}
				else if(token[j].equals("add") || token[j].equals("sub") || token[j].equals("slt") || token[j].equals("or") || token[j].equals("and")
						|| token[j].equals("sw") || token[j].equals("lui") || token[j].equals("jump") || token[j].equals("nop"))
				{
					break;
				}
			}
			
			if(BranchFound == true)
			{
				boolean FirstNOPfound = false;
				boolean SecondNOPfound = false;
			
				if(i < Input.length - 2 && Input[i+1] != null && Input[i+2] != null )
				{
					String[] token2 = Input[i+1].split(" ");
					String[] token3 = Input[i+2].split(" ");
					
					for(int g = 0; g < token2.length; g++)
					{
						if(token2[g].equals("nop"))
						{
							FirstNOPfound = true;
							break;
						}
					}
					for(int g = 0; g < token3.length; g++)
					{
						if(token3[g].equals("nop"))
						{
							SecondNOPfound = true;
							break;
						}
					}
				}
				
				Temp[k] = Input[i];
				k++;
				
				
				if(FirstNOPfound == false)
				{
					Temp[k] = "nop";
					k++;
				}
				if(SecondNOPfound == false)
				{
					Temp[k] = "nop";
					k++;
				}
			}
			else if(LWHazardFound ==  true)
			{
				Temp[k] = Input[i];
				k++;
				
				Temp[k] = "nop";
				k++;
				
			}
			else
			{
				Temp[k] = Input[i];
				k++;
			}
			i++;
		}
		
		return Temp;
	}
	
	//Formats the String so that the number is first and the location or instruction is 
	//followed by the appropriate registers and data
	public static String[] NumberInserter(String[] Input)
	{
		
		int i = 0;
		String NewNumberedString = null;
		while(Input[i] != null)
		{
			String[] token  = Input[i].split(" ");
			
			for(int j = 0; j < token.length; j++)
			{
				if(token[j].charAt(token[j].length()-1) == ':')
				{
					NewNumberedString = Integer.toString(i);
					for(int k = 0; k < token.length - j; k++)
					{
						NewNumberedString = NewNumberedString + " " + token[j+k];
					}
					break;
				}
				else if(token[j].equals("add") || token[j].equals("sub") || token[j].equals("slt") || 
						token[j].equals("or") || token[j].equals("and") || token[j].equals("lw") ||
						token[j].equals("sw") || token[j].equals("beq") || token[j].equals("bne") ||
						token[j].equals("lui") || token[j].equals("jump") || token[j].equals("nop"))
				{
					NewNumberedString = Integer.toString(i);
					for(int k = 0; k < token.length - j; k++)
					{
						NewNumberedString = NewNumberedString + " " + token[j+k];
					}
					break;
				}
			}
			Input[i] = NewNumberedString;
			i++;
		}
		for(int y = 0; y < NumOfInstructions2; y++)
			FormattedInstructions[y] = Input[y];
		return Input;
	}
	
	//records the numeric value of a location and saves it to an array
	public static String[] LocationRecorder(String[] Input)
	{
		int i = 0;
		while(Input[i] != null)
		{
			String[] token = Input[i].split(" ");
			String NewFormattedString = null;
			
			if(token[1].charAt(token[1].length()-1) == ':')
			{
				String Temp = token[1].substring(0, token[1].length()-1);
				location[NumOfLocations] = new location(Temp, Integer.parseInt(token[0]));
				NumOfLocations++;
				NewFormattedString = token[0];
				for(int j = 2; j < token.length; j++)
				{
					NewFormattedString = NewFormattedString + " " + token[j];
				}
			}
			else
			{
				NewFormattedString = token[0];
				for(int j = 1; j < token.length; j++)
				{
					NewFormattedString = NewFormattedString + " " + token[j];
				}
			}
			
			Input[i] = NewFormattedString;
			
			i++;
		}
		
		return Input;
		
	}
	
	// Parsing of the Current Line
	public static String[] ConvertToMachineCode(String[] Input )
	{
		int k = 0;
		while(Input[k] != null)
		{
			String[] tokens = Input[k].split(" ");
	
			// Operation - R Type
			if (tokens[1].equals("add") )
			{
			
				String opcode = "000000";
				String Rs = RegisterBinary(tokens[3]);
				String Rt = RegisterBinary(tokens[4]);
				String Rd = RegisterBinary(tokens[2]);
				String shamt = "00000";
				String Funct =  "000000";
				
				Input[k] = opcode + Rs + Rt + Rd + shamt + Funct;
			}
			else if (tokens[1].equals("sub") )
			{
				String opcode = "000000";
				String Rs = RegisterBinary(tokens[3]);
				String Rt = RegisterBinary(tokens[4]);
				String Rd = RegisterBinary(tokens[2]);
				String shamt = "00000";
				String Funct =  "000001";
				
				Input[k] = opcode + Rs + Rt + Rd + shamt + Funct;
				
			}
			else if (tokens[1].equals("slt") )
			{
				String opcode = "000000";
				String Rs = RegisterBinary(tokens[3]);
				String Rt = RegisterBinary(tokens[4]);
				String Rd = RegisterBinary(tokens[2]);
				String shamt = "00000";
				String Funct =  "000010";
				
				Input[k] = opcode + Rs + Rt + Rd + shamt + Funct;
			}
			else if (tokens[1].equals("or") )
			{
				String opcode = "000000";
				String Rs = RegisterBinary(tokens[3]);
				String Rt = RegisterBinary(tokens[4]);
				String Rd = RegisterBinary(tokens[2]);
				String shamt = "00000";
				String Funct =  "000011";
				
				Input[k] = opcode + Rs + Rt + Rd + shamt + Funct;
			}
			else if (tokens[1].equals("and") )
			{
				String opcode = "000000";
				String Rs = RegisterBinary(tokens[3]);
				String Rt = RegisterBinary(tokens[4]);
				String Rd = RegisterBinary(tokens[2]);
				String shamt = "00000";
				String Funct =  "000100";
				
				Input[k] = opcode + Rs + Rt + Rd + shamt + Funct;
			}
			// Operation - I Type
			else if (tokens[1].equals("lw") )
			{
				if(tokens[3].charAt(tokens[3].length()-1) == ')')
				{
					char[] CharArray = tokens[3].toCharArray();
					for(int t = 0; t < CharArray.length; t++)
					{
						if(CharArray[t] == '(')
						{
							CharArray[t] = ' ';
							tokens[3] = new String(CharArray);
							break;
						}
					}
					String[] IndexMode = tokens[3].split(" ");
					String opcode = "000001";
					String Rs = RegisterBinary(IndexMode[1].substring(0, IndexMode[1].length()-1));
					String Rt = RegisterBinary(tokens[2]);
					String Immediate = Integer.toBinaryString(Integer.parseInt(IndexMode[0]));
					Immediate = AddZeros(Immediate, 16);
					Input[k] = opcode + Rs + Rt + Immediate;
				}
				else
				{
					String opcode = "000001";
					String Rs = "00000";
					String Rt = RegisterBinary(tokens[2]);
					String Immediate = Integer.toBinaryString(Integer.parseInt(tokens[3]));
					Immediate = AddZeros(Immediate, 16);
					Input[k] = opcode + Rs + Rt + Immediate;
				}
		
				
				
			}
			else if (tokens[1].equals("sw") )
			{
				if(tokens[3].charAt(tokens[3].length()-1) == ')')
				{
					char[] CharArray = tokens[3].toCharArray();
					for(int t = 0; t < CharArray.length; t++)
					{
						if(CharArray[t] == '(')
						{
							CharArray[t] = ' ';
							tokens[3] = new String(CharArray);
							break;
						}
					}
					String[] IndexMode = tokens[3].split(" ");
					String opcode = "000010";
					String Rs = RegisterBinary(IndexMode[1].substring(0, IndexMode[1].length()-1));
					String Rt = RegisterBinary(tokens[2]);
					String Immediate = Integer.toBinaryString(Integer.parseInt(IndexMode[0]));
					Immediate = AddZeros(Immediate, 16);
					Input[k] = opcode + Rs + Rt + Immediate;
				}
				else
				{
					String opcode = "000010";
					String Rs = "00000";
					String Rt = RegisterBinary(tokens[2]);
					String Immediate = Integer.toBinaryString(Integer.parseInt(tokens[3]));
					Immediate = AddZeros(Immediate, 16);
					Input[k] = opcode + Rs + Rt + Immediate;
				}
			}
			else if (tokens[1].equals("beq") )
			{
				String opcode = "000011";
				String Rs = RegisterBinary(tokens[2]);
				String Rt = RegisterBinary(tokens[3]);
				String Immediate;
				try{
					Immediate = Integer.toBinaryString(Integer.parseInt(tokens[4]));
				}catch(NumberFormatException e){
					Immediate = FindAddress(tokens[4], Integer.parseInt(tokens[0]));
				}
				Immediate = AddZeros(Immediate, 16);
				Immediate = RemoveOnes(Immediate, 16);
				
				Input[k] = opcode + Rs + Rt + Immediate;
			}
			else if (tokens[1].equals("bne") )
			{
				String opcode = "000100";
				String Rs = RegisterBinary(tokens[2]);
				String Rt = RegisterBinary(tokens[3]);
				String Immediate;
				try{
					Immediate = Integer.toBinaryString(Integer.parseInt(tokens[4]));
				}catch(NumberFormatException e){
					Immediate = FindAddress(tokens[4], Integer.parseInt(tokens[0]));
				}
				Immediate = AddZeros(Immediate, 16);
				Immediate = RemoveOnes(Immediate, 16);
				
				Input[k] = opcode + Rs + Rt + Immediate;
			}
			else if (tokens[1].equals("lui") )
			{
				String opcode = "000110";
				String Rs = "00000";
				String Rt = RegisterBinary(tokens[2]);
				String Immediate = Integer.toBinaryString(Integer.parseInt(tokens[3]));
				Immediate = AddZeros(Immediate, 16);
				Immediate = RemoveOnes(Immediate, 16);
				
				Input[k] = opcode + Rs + Rt + Immediate;
			}
			else if (tokens[1].equals("jump") )
			{
				
				String opcode = "000101";
				String Immediate;
				try{
					Immediate = Integer.toBinaryString(Integer.parseInt(tokens[2]));
				}catch(NumberFormatException e){
					Immediate = FindAddress(tokens[2], Integer.parseInt(tokens[0]));
				}
				
				Immediate = AddZeros(Immediate, 26);
				Immediate = RemoveOnes(Immediate, 26);
				
				Input[k] = opcode + Immediate;
			}
			
			else if(tokens[1].equals("nop"))
				Input[k] = "00011000000111110000001111111111";
			
			else
				System.out.println("Unrecognized Instruction");
			
			k++;
		}
		for(int i = 0; i < NumOfInstructions2; i++)
			if(Input[i] != null)
			{
				System.out.println(FormattedInstructions[i]);
				System.out.println(Input[i]);
			}
		return Input;
	}
	
	//Converts a register value to a binary value
	public static String RegisterBinary(String st)
	{
		// Instantiate register
		String register = "";
	
		// Loop through removing the $
		for( int j = 1; j < st.length(); j++)
		{
			char s = st.charAt(j);
			register = register + s;
		}
		int t = Integer.parseInt(register);
		
		
		String binary = Integer.toBinaryString(t);
		
		binary = AddZeros(binary, 5);
	
		return binary;
	}
	
	// Find the correct Branch / Jump Address
	public static String FindAddress(String s, int number)
	{
		String Address = "";
		
		
		// Need to understand which String we are jumping to
		for(int i = 0; i < NumOfLocations; i++)
		{
			
			if(s.equals(location[i].getName()))
			{
				number = location[i].getLocation() - number;
				Address = Integer.toBinaryString(number);
			}
		}
		
		return Address;
	}
	
	// Check the length of the current registers
	public static String AddZeros(String s, int i)
	{
		while( s.length() < i )
				s = "0"+s;
		return s;
	}
	
	//removes excess ones from the front of a string
	public static String RemoveOnes(String s, int i)
	{
		char[] temp = null;
		if(s.length() != i ){
				temp = s.toCharArray();
				s = new String(temp, temp.length - i, i);
		}
		return s;
	}
	
	//Formats the original string creating only a space in between any text so that .split function can be used more efficiently
	public static String[] SpaceFormatter(String[] LineToBeSpaced) {
		
		
		
		LineToBeSpaced = TabConverter(LineToBeSpaced);
		
		for(int p = 0; p < NumOfInstructions2; p++){
			int i = 0;	//index maintains the main spot in the string
			int j = 0;	//index holds the number of space when a multiple space is found
			int k = 0;	//index used for determining how much of the string is left
			int l = 0;	//place holder used move the string over 
			
			int endspaces = 0;	//counts all the spaces at the end
			boolean EOL = false;	//flag is at end of line
			String FormattedString = null;
			
			if(LineToBeSpaced[p] != null)	//only formats if there is a string
			{
				
				LineToBeSpaced[p] = LineToBeSpaced[p] + "                                                  "; //adds space to end so that extra spaces will added to the end
				
				char[] CharArray =  LineToBeSpaced[p].toCharArray();	//converts to char array for easy use
				
				while(i < CharArray.length){	//main while loop 
					
					if(CharArray.length > i+1){ //makes sure we are still in bounds of array
						
						if(CharArray[i] == ' ' && CharArray[i+1] == ' '){	//checks for multiple spaces
							
							j = 1;
							while(CharArray[i+j] == ' '){	//while loop checks to see only spaces remain
							
								j++;
								if(CharArray.length <= i+j){	//end is reached condition
								
									endspaces = j; //records spaces to be cut off the end
									EOL = true;	//sets the flag
									break;
								}
							}
							
							if(EOL == false){ //case that there is more characters beside spaces
								
								j = 1;
								while(CharArray[i+j] == ' '){	//while loop to count the number of spaces
									
									j++;
								}
							
								j = j-1;	//adjust j to only delete extra spaces not all spaces
								l = i+1;
								
								for(k = 0; k < (CharArray.length - (i+j)); k++)	//for loop moves the remaining characters 
								{												//to the left depending on how many extra spaces were found
									if(CharArray.length > l+j ){				//and puts the extra spaces at the end
										CharArray[l] = CharArray[l+j];
										l++;
									}
								}
							}//end of if(EOL == false)
						}//end of if(CharArray[i] == ' ' && CharArray[i+1] == ' ')
					}// end of if(CharArray.length > i+1)
					i++;
					if(EOL == true){	// end has been reached stop checking
						break;	
					}
					
				}//end of main while loop
				
				FormattedString = new String(CharArray, 0, CharArray.length -endspaces); //creates the new string
				
				FormattedString = FormattedString.trim();
				
				LineToBeSpaced[p] = FormattedString;
			}
			LineToBeSpaced[p] = FormattedString;
		}
		
		LineToBeSpaced = WhiteSpaceRemover(LineToBeSpaced);
		
		return LineToBeSpaced;
	}// End of SpaceFormatter Function;
	
	//converts tab to spaces so that they can be formatted in the space formatter function
	public static String[] TabConverter(String[] Input)
	{
		
		
		int i = 0;
		for(int p = 0; p < NumOfInstructions2; p++){
			
			if(Input[p] != null)
			{
				char[] CharArray = Input[p].toCharArray();
				i = 0;
				while(i < CharArray.length)
				{
					if(CharArray[i] == '\t')
					{
						CharArray[i] = ' ';
					}
					i++;
						
				}
				
				Input[p] = new String(CharArray);
			}
			
		}
		
		return Input;
	}
	
	//removes unnecessary lines
	public static String[] WhiteSpaceRemover(String[] Input)
	{
		String[] Temp = new String[NumOfInstructions2];
		
		int i = 0;
		int k = 0;
		int p = 0;
		while(Input[i] != null)
		{
			boolean WhiteSpace = true;
			if(!Input[i].equals("") && !Input[i].equals(" ") && !Input[i].equals("\t"))
			{
				
				String[] token = Input[i].split(" ");
				
				for(k = 0; k < token.length; k++)
				{
					if(token[k].charAt(token[k].length()-1) == ':')
					{
						WhiteSpace = false;
						break;
					}
					
					if(token[k].equals("add") || token[k].equals("sub") || token[k].equals("slt") || token[k].equals("or") || token[k].equals("and") ||
							token[k].equals("lw") || token[k].equals("sw") || token[k].equals("beq") || token[k].equals("bne") || token[k].equals("lui")
							|| token[k].equals("jump") || token[k].equals("nop"))
					{
						WhiteSpace = false;
						break;
					}
				}
				
				if(WhiteSpace == false)
				{
					Temp[p] = Input[i];
					p++;
				}
			}
			i++;
			
		}
		
		return Temp;
	}
	

}
	

	