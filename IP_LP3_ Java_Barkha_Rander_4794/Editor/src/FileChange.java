import java.io.*;
import javax.swing.filechooser.FileFilter;

public class FileChange extends FileFilter
{
private String extension;
private String description;

public FileChange()
{
setExtension(null);
setDescription(null);
}
public FileChange(final String ext, final String desc)
{
	setExtension(ext);
	setDescription(desc);
}
public boolean accept(File f)
{
	final String filename=f.getName();
	
	if(	f.isDirectory() || extension==null || filename.toUpperCase().endsWith(extension.toUpperCase()))
		return true;
	return false;

}
public String getDescription()
{
	return description;
}

public void setDescription(String desc)
{
	if(desc==null)
		description=new String("All Files(*.*)");
	else
		description=new String(desc);
}

public void setExtension(String ext)
{
	if(ext==null)
		{extension=null;  return;}
	
	extension=new String(ext).toLowerCase();
	if(!ext.startsWith("."))
		extension="."+extension;
	}

}
