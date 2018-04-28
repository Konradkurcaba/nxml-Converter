package converter;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import java.io.*;
import java.nio.file.Files;
import org.dom4j.*;
import org.dom4j.Document;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;


public class main {
    static String  destination_path;
    public static void convert(File file)
    {
        try
        {
            //read and prepare file
            String file_content = new String ( Files.readAllBytes( file.toPath() ) );
            file_content = file_content.replace("archivearticle.dtd", "\\root\\dtd\\archivearticle.dtd");
            SAXReader reader = new SAXReader();
            Document document = DocumentHelper.parseText(file_content);
            Element root = document.getRootElement();
           
            
            // get id and title
            
            String doc_id = document.selectSingleNode("//article/front/article-meta/article-id[@pub-id-type='pmc']").getText();
            String doc_title = document.selectSingleNode("//article/front/article-meta/title-group/article-title/text()").getText();
            
            
            // get body
            
            Element body = root.element("body");
            String content = treeWalk(body);
            
            // create new document
            
            Document new_document = DocumentFactory.getInstance().createDocument();
            Element new_root = new_document.addElement("doc");
            
            Element new_id = new_root.addElement("doc_id").addText(doc_id);
            Element new_title = new_root.addElement("title").addText(doc_title);
            Element new_body = new_root.addElement("body").addText(content);
            
            // write to file
            FileOutputStream fos = new FileOutputStream("destination_path\\"+ new_id +".tr");
            OutputFormat format = OutputFormat.createPrettyPrint();
            XMLWriter writer = new XMLWriter(fos, format);
            writer.write( new_document);
            writer.flush();
            

            
            
        
        }catch (Exception e)
        {
            System.out.print(e.toString());
        }
    }
    public static String treeWalk(Element element) {
    String output = "";
    for (int i = 0, size = element.nodeCount(); i < size; i++) {
        
        Node node = element.node(i);
        if (node instanceof Element) {
            
           output = output + treeWalk((Element) node);
            
        }
        else {
            output = output + node.getText();
            
        }
    }
    return output;
}
    public static void main(String[] args)
    {
        if(args.length != 2)
        {
            System.out.print("Need source and destination path");
            return;
        }
        destination_path = args[1];
        File input_directory = new File(args[0]);
        File[] dir_list = input_directory.listFiles();
        
        
        
        
        
        
        File input_file =new File("C:\\xd\\1.nxml");
        convert(input_file);
    }
}
    class dir_walk implements Runnable
    {
    File dir;
    
    dir_walk(File dir)
    {
        this.dir = dir; // main folders
    }
        
    @Override
    public void run() {
    
        File[] dir_list = dir.listFiles(); // number folders
        
        for(File n_dir : dir_list)
        {
            File[] file_list = n_dir.listFiles(); // files
            
            
            for(File file : file_list)
            {
                main.convert(file); 
            }
            
        }
        }
    }