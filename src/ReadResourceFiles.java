import java.awt.List;
import java.io.*;
import java.util.ArrayList;


import org.jdom2.*;
import org.jdom2.input.SAXBuilder;


public class ReadResourceFiles {

	public static List listVarName;
	public static List ListMsgVarName;
	public static List ListMsgSpecialVarName;
	public static ArrayList<File> ListPlistFile = new ArrayList<File>();

	public static String[] fileType		=	{".plist",".png"};
	public static String[] specialChar	=	{"-","@",".","(",")"};


	/////Purpose: snippet code used to process if file is places in many sub-folder.
	public static List loopReadDirectoryFile(File _curDirectoryFolder,String _currentPath, List listString){
		File[] listSubFiles = _curDirectoryFolder.listFiles();
		String path = "";



		for(File file : listSubFiles){
			if (file.isFile()){
				String _nameFile = file.getName();
				for(int i = 0;i<fileType.length;i++){
					if (_nameFile.endsWith(fileType[i])){
						if (i == 0){
							ListPlistFile.add(file);
						}
						path = _currentPath + "/" + file.getName();
						listString.add(path);
						listString.add(file.getName());
					}
				}
			}else if (file.isDirectory()){
				listString = loopReadDirectoryFile(file,_currentPath + "/" + file.getName(), listString);
			}
		}

		return listString;
	}


	/////Purpose: use to read .plist file
	/////This use library Jdom-2.0.6
	public static ArrayList<String> ReadPlistFile(File _plistFile) {
		ArrayList<String> listResourceFile = new ArrayList<String>();

		try {
			SAXBuilder saxBuilder = new SAXBuilder();

			//System.out.println(_plistFile.getName());

			Document document = saxBuilder.build(_plistFile);

			//System.out.println("Root element :" + document.getRootElement().getName());

			Element classElement = document.getRootElement();

			ArrayList<Element> resourceList = new ArrayList<Element>();

			Element checkIsPlistImage = classElement.getChild("dict").getChild("key");
			//System.out.println(checkIsPlistImage.getText());
			if (checkIsPlistImage.getText().contains("frames")){
				for(Element object : classElement.getChild("dict").getChild("dict").getChildren()){
					if (object.getName() == "key"){
						resourceList.add(object);
					}

				}

				// System.out.println("----------------------------");

				for (Element resource : resourceList) {    

					//System.out.println("\nCurrent Element :" + resource.getText());
					listResourceFile.add(resource.getText());
				}
			}

		}catch(JDOMException e){
			e.printStackTrace();
		}catch(IOException ioe){
			ioe.printStackTrace();
		}

		return listResourceFile;
	}

	//////Purpose: snippet code use to read a file then store each lines into a list string.
	private static ArrayList<String> readFile(String filename)
	{
		ArrayList<String> records = new ArrayList<String>();
		try
		{
			BufferedReader reader = new BufferedReader(new FileReader(filename));
			String line;
			while ((line = reader.readLine()) != null)
			{
				records.add(line);
			}
			reader.close();
			return records;
		}
		catch (Exception e)
		{
			System.err.format("Exception occurred trying to read '%s'.", filename);
			e.printStackTrace();
			return null;
		}
	}

	/////Purpose: snippet code to support check similar var names.
	public static int hasSameVarName(String _checkName){
		int repeatNameCount = 0;

		for(int i = 0; i < listVarName.getItemCount();i++){
			if (_checkName.length() == listVarName.getItem(i).length()){
				if (_checkName.equals((String)listVarName.getItem(i))){
					repeatNameCount++;
					System.out.println(repeatNameCount);
				}

			}
		}



		return repeatNameCount;
	}


	/////Purpose: find if var name contain any special characters, then replace with '_'
	public static String replaceSpecialChar(String _varName){
		String varName = _varName;
		for(int z = 0; z < specialChar.length;z++){
			if (varName.contains(specialChar[z])){
				ListMsgSpecialVarName.add(varName);
				for(int i = 0; i < varName.length(); i++){
					char tempChar = varName.charAt(i);
					if (tempChar == '-' || tempChar == '@'){
						varName = varName.replace(varName.charAt(i), '_');
					}else if (tempChar == '.'){
						varName = varName.replace(varName.charAt(i), 'x');
					}else if (tempChar == '(' || tempChar == ')'){
						varName = varName.replace(varName.charAt(i), 'y');
					}
				}

				//System.out.println(varName);
			}
		}


		return varName;
	}


	////Purpose: Check if some var name are similar and automatically change their name.
	public static String changeVarNameIfHasTheSame(String _varName){

		String varName = replaceSpecialChar(_varName);	//Check whether var name has a special character : - or @
		String finalName = varName;
		
		int countRepeatName = hasSameVarName(varName);
		int count = 1;
		int countRepeat = 0;
		
		if (countRepeatName > 0){
			countRepeat++;
			while(count != 0){
				count = 0;
				varName = finalName + String.format("_%d", countRepeat);
				//System.out.println(varName);
				
				countRepeatName = hasSameVarName(varName);
				if (countRepeatName>0){
					countRepeat++;
					count++;
				}
			}
			
			ListMsgVarName.add(varName.substring(19, varName.length()));
			
		}

		return varName;
	}

	////Purpose: When user open a folder, check to find necessary files, if not : throw ERROR MSG.
	public static int hasAnyAppriciateFile(File _sourceFile){
		File folder = _sourceFile;
		File[] listOfFiles = folder.listFiles();
		int countAppriciateFile = 0;

		for(File file : listOfFiles){
			if (file.isFile()){
				if (file.getName().endsWith(".png") || file.getName().endsWith(".plist")){
					countAppriciateFile++;
				}
			}else if (file.isDirectory()){
				File[] listOfChildFile = file.listFiles();
				for(File childFile : listOfChildFile){
					if (childFile.getName().endsWith(".png") || childFile.getName().endsWith(".plist")){
						countAppriciateFile++;
					}
				}
			}
		}
		return countAppriciateFile;
	}

	////Purpose: Code written in new file would look nice !
	private static String createIntent(String _string){
		int length = _string.length();

		String space = " ";
		for(int i = 0;i<91-length;i++){
			space+=" ";
		}
		return space;
	}

	public static void Create(File _file,String _destinationPath, List _list) throws IOException  {
		File folder = _file;
		File[] listOfFiles = folder.listFiles();
		
		String fileName	    = "ResourcesNew.h";
		String pngType 		= "static const char* srcPNG_";
		String plistType	= "static const char* srcPLIST_";

		String[] varType		= {plistType,pngType};

		int[] subVarChar = {6,4}; // ~ .plist has 6 char + .png has 4 char

		ArrayList<String> recordsStart 	= readFile("Resource/startDescription");	//Get content from startDescription.text
		ArrayList<String> recordsEnd 	= readFile("Resource/endDescription");		//Get content from endDescription.text

		//init list contains var name
		listVarName 	= new List();
		ListMsgVarName 	= new List();
		ListMsgSpecialVarName = new List();
		
		BufferedWriter out = new 
				BufferedWriter(new FileWriter(_destinationPath+"/" + fileName));	//Write to new file named "Resources New.h"

		out.write("#ifndef __RESOURCE_NEW_H__");
		out.newLine();
		out.write("#define __RESOURCE_NEW_H__");
		for(String object : recordsStart) {		//Write content in startDescription.text to "Resources New.h"
			out.write(object);
			out.newLine();
		}

		out.newLine();
		out.newLine();
		out.newLine();

		String _name;
		List listString = new List();
		int _indexFileType = 0;


		///////Parse data to add all filePath and varName to listString, then we will parse listString
		for (int i = 0; i < listOfFiles.length; i++) {
			if (listOfFiles[i].isFile()) {	//If file is not in any sub folders, create code string immediately
				_name = listOfFiles[i].getName();
				for(int fileTypeIndex = 0; fileTypeIndex<fileType.length;fileTypeIndex++){
					if (_name.endsWith(fileType[fileTypeIndex])){
						String varName	= varType[_indexFileType] +  _name.substring(0, _name.length() - subVarChar[_indexFileType]);
						varName = changeVarNameIfHasTheSame(varName);
						listVarName.add(varName);
						listString.add(_name);
						listString.add(varName);

						if (fileTypeIndex == 0){
							ListPlistFile.add(listOfFiles[i]);
						}
					}
				}
			} else if (listOfFiles[i].isDirectory()) {	//Process file if this is a directory folder.
				listString = loopReadDirectoryFile(listOfFiles[i],listOfFiles[i].getName(), listString);
			}
		}

		///////Parse listString with index - r/2 = 0 -> path, r/2 = 1 -> var name
		for(String _fileType : fileType){
			for(int r = 0; r < listString.getItemCount();r+=2){
				_name = listString.getItem(r+1);
				if (_name.endsWith(_fileType)){
					_list.add(listString.getItem(r)); //Display file name in UI.
					_list.select(_list.getItemCount());

					String fullPath 	= listString.getItem(r);
					String varName	= varType[_indexFileType] + _name.substring(0, _name.length() - subVarChar[_indexFileType]);
					varName = changeVarNameIfHasTheSame(varName);
					listVarName.add(varName);
					out.write(varName + createIntent(varType[_indexFileType]+varName) + " = " + "\"" + fullPath + "\";"  );
					out.newLine();
				}
			}

			_indexFileType++;

			out.newLine();
			out.newLine();
			out.newLine();
		}

		out.newLine();


		////////////Read files in .plist and write to file
		for (int i = 0;i< ListPlistFile.size();i++){
			ArrayList<String> listResourceInPlist = ReadPlistFile(ListPlistFile.get(i));

			_name = ListPlistFile.get(i).getName();
			out.write("#pragma region " + _name.substring(0,_name.length()-6));
			out.newLine();

			for(int z = 0; z < listResourceInPlist.size();z++){
				_name = listResourceInPlist.get(z);
				String varName	= pngType +  _name.substring(0, _name.length() - 4);
				varName = changeVarNameIfHasTheSame(varName);
				listVarName.add(varName);
				out.write(varName + createIntent(pngType+varName) + " = " + "\"" + _name + "\";"  );
				out.newLine(); 
				_list.add(_name); //Display file name in UI.
				_list.select(_list.getItemCount());
			}
			out.write("#pragma endregion ");

			out.newLine();
			out.newLine();
			out.newLine();
		}


		///////If has some similar var names -> display WARNING to window
		if (ListMsgVarName.getItemCount()>0){
			_list.add("==========================");
			_list.add("=> WARNING: Found some resources have the same names");
			for(int i = 0; i < ListMsgVarName.getItemCount();i++){
				_list.add(ListMsgVarName.getItem(i));
				_list.select(_list.getItemCount());
			}
			_list.add(" ");
			_list.select(_list.getItemCount());
		}
		//*


		///////If has some special characters -> display WARNING to window
		if (ListMsgSpecialVarName.getItemCount()>0){
			_list.add("==========================");
			_list.add("=> WARNING: Found some resources contrain special character");
			for(int i = 0; i < ListMsgSpecialVarName.getItemCount();i++){
				_list.add(ListMsgSpecialVarName.getItem(i).substring(19, ListMsgSpecialVarName.getItem(i).length()));
				_list.select(_list.getItemCount());
			}
			_list.add(" ");
			_list.select(_list.getItemCount());
		}
		//*

		for(String object : recordsEnd) {
			out.write(object);
			out.newLine();
		}


		out.close();
		System.out.println("File created successfully");
	}
}


