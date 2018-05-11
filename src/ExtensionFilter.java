/*
 * Copyright © 2008 Ron de Jong (ronuitzaandam@gmail.com).
 *
 * This is free software; you can redistribute it 
 * under the terms of the Creative Commons License
 * Creative Commons License: (CC BY-NC-ND 4.0) as published by
 * https://creativecommons.org/licenses/by-nc-nd/4.0/ ; either
 * version 4.0 of the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the 
 * Creative Commons Attribution-NonCommercial-NoDerivatives 4.0
 * International Public License for more details.
 *
 * You should have received a copy of the Creative Commons 
 * Public License License along with this software;
 */

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
