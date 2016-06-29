
import java.io.File;
import javax.swing.filechooser.FileFilter;

/**
 *
 * @author ron
 */
public class ExtensionFilter extends FileFilter
{

    private String extension;
    private String description;

    /**
     *
     * @param extensionParam
     * @param descriptionParam
     */
    public ExtensionFilter(String extensionParam, String descriptionParam)
    {
        extension   = extensionParam.toLowerCase();
        description = descriptionParam;
    }

    @Override
    public boolean accept(File f) {
        return (f.isDirectory()||f.getName().toLowerCase().endsWith(extension));
    }

    @Override
    public String getDescription() {
        return description;
    }

}
