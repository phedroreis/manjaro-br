package br.com.hkp.manjaro;

import static br.com.hkp.manjaro.Util.DOWNLOAD_DIR;
import br.com.hkp.manjaro.Util.HtmlFilter;
import static br.com.hkp.manjaro.Util.readTextFile;
import static br.com.hkp.manjaro.Util.systemErrPrintln;
import static br.com.hkp.manjaro.Util.writeTextFile;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/******************************************************************************
 * Constroi a copia estatica e baixa do servidor os arquivos que sao linkados 
 * pelas paginas do forum.
 * 
 * @since 14 de março de 2021 v1.0
 * @version 1.0
 * @author "Pedro Reis"
 *****************************************************************************/
public class Build
{
    private static final Pattern HREF = 
        Pattern.compile("href=\"(.+?)\"");
    
    private static final Pattern HREFACTION = 
        Pattern.compile("(href|action)=\"(.+?)\"");
    
    private static final Pattern POST =
        Pattern.compile("-t\\d+(-\\d+)*\\.html#(p\\d+|wrapper)");
    
    private static final Pattern TOPIC =
        Pattern.compile("-t\\d+(-\\d+)*\\.html");
   
        
    private static void editHtml()
    {
        /*
        A estrutura mapeia urls de links que serao editados. Cada entrada nesse
        map e um par key/value onde key eh a url original e value recebe e a url
        modificada que irah substituir a original
        */
        HashMap<String, String> HREF_MAP = new HashMap<>();
        
        /*
        Obtem o diretorio onde estao os HTMLs que serao editados
        */
        File downloadDir = new File(DOWNLOAD_DIR);
        
        /*
        Um array com todos os HTMLs do diretorio
        */
        File[] listFiles = downloadDir.listFiles(new HtmlFilter());
        
        String[] sections = downloadDir.list(new FileFilter());
        
        for(int i = 0; i < sections.length; i++)
        {
            sections[i] = "href=\"" + sections[i].replace("page0.html", "/");
        }
        
        /*
        Processa todos os HTMLs do diretorio.
        */
        for(File htmlFile: listFiles)
        {
            /*
            Obtem nome do arquivo que sera editado no corpo do loop for
            */
            String filename = htmlFile.getName();
            
            System.out.println("Processando: " + filename + "\n");
            
            String htmlContent;
            
            /*
            Le o conteudo de um arquivo HTML para dentro da String htmlContent
            */
            try { htmlContent = readTextFile(htmlFile); }
            catch(IOException e) { systemErrPrintln(e.getMessage()); break; }
            
            /*
            Urls que apontam para https:\\manjaro-linux.com.br\forum\ vao passar
            a apontar para o diretorio corrente na copia local. Ou seja, o
            diretorio onde serao armazenados todos os HTMLs baixados do forum.
            */
            htmlContent = 
                htmlContent.replaceAll
                (
                    "https?:\\/\\/(www\\.)?manjaro-linux\\.com\\.br\\/forum\\/", 
                    ""
                );
            
            /*
            Url relativa ao diretorio corrente no servidor tambem passa a 
            apontar para onde estao os HTMLs na copia local
            */
            htmlContent = htmlContent.replaceAll("=\" *./", "=\"");
            
            
            /*
            Urls que apontam para o diretorio pai de /forum no servidor vao 
            passar a apontar para o pai do diretorio corrente na copia local.
            /forum serah o diretorio corrente dos HTMLs na copia local.
            */
            htmlContent = 
                htmlContent.replaceAll
                (
                    "https?:\\/\\/(www\\.)?manjaro-linux\\.com\\.br\\/", 
                    "../"
                );
            
            /*
            Urls que apontam para a raiz do servidor passam a apontar para a
            pagina de entrada do forum portal.html
            */
            htmlContent = 
                htmlContent.replaceAll
                (
                    "https?:\\/\\/(www\\.)?manjaro-linux\\.com\\.br", 
                    "portal.html"
                );
            
            /*
            Deleta o prefixo dos nomes de arquivos para avatares
            */
            htmlContent = htmlContent.replace("file.php?avatar=", "");
            
            /*
            Deleta as sid das URLs. Evita que o mesmo arquivo seja referenciado
            com mais de um nome.
            */
            htmlContent = 
                htmlContent.replaceAll("&amp;sid=[a-f0-9]+?\"", "\"");

            htmlContent = 
                htmlContent.replaceAll("\\?sid=[a-f0-9]+?\"", "\"");
            
            /*
            Renomeia o link para o arquivo CSS principal que eh enviado pelo 
            servidor por meio de uma requisicao PHP
            */
            htmlContent = 
                htmlContent.replaceAll("style\\.php\\?.+?\"", "style.css\"");
           
            /*
            Substitui kb.php?etc... por kb.html somente. Este arquivo sera 
            renomeado na copia local para kb.html
            */
            htmlContent = 
                htmlContent.replaceAll
                (
                    "href=\"kb\\.php\\?.+?\"", 
                    "href=\"kb.html\""
                ).replace("kb.php", "kb.html");
            
            /*
            Renomeia referencias a portal.php e index.php para os nomes
            que estes arquivos receberao na copia local
            */
            htmlContent = 
                htmlContent.replace("portal.php", "portal.html").
                            replace("index.php", "index.html");
            /*
            Links para grupos de usuarios
            */
            htmlContent =
                htmlContent.replaceAll("\\w+-g\\d+\\.html", "default.html");
           
            /*
            Links para paginas de membros do forum sao redirecionados para
            default.html
            */
            htmlContent = 
                htmlContent.
                replaceAll("\"member\\/.+?\\/\"", "\"default.html\"");
            
            /*
            Substitui postxxx.html#pxxx por default.html
            */
            htmlContent = 
                htmlContent.replaceAll
                (
                    "href=\"post\\d+\\.html.+?\"", 
                    "href=\"default.html\""
                );
            
            /*
            Substitui links para funcoes PHP por link para default.html
            */
            htmlContent = 
                htmlContent.replaceAll
                (
                    "(thankslist|faq|viewtopic|viewforum|" +
                    "ucp|form|search|posting|feed)\\.php.*?\"", 
                    "default.html\""
                );
            
            /*
            Edita links para secoes e subsecoes
            */
            htmlContent = 
                htmlContent.replace
                (
                    "/page", 
                    "page"
                );  
            
            
            /*
            Edita links para posts
            */

            Matcher m = HREF.matcher(htmlContent);

            while (m.find())
            {
                String url = m.group(1);

                if (url.contains("http")) continue;

                Matcher m2 = POST.matcher(url);

                if (m2.find())
                {
                    String post = m2.group();
                    HREF_MAP.put(url, post);
                }
            }//while

            for(String url: HREF_MAP.keySet())
            {
                htmlContent = htmlContent.replace(url, HREF_MAP.get(url));
            }//for

            /*
            Edita links para topicos
            */

            HREF_MAP = new HashMap<>(); 

            m = HREFACTION.matcher(htmlContent);

            while (m.find())
            {
                String url = m.group(2);

                if (url.contains("http")) continue;

                Matcher m2 = TOPIC.matcher(url);

                if (m2.find())
                {
                    String topic = m2.group();
                    HREF_MAP.put(url, topic);
                }
            }//while

            for(String url: HREF_MAP.keySet())
            {
                htmlContent = htmlContent.replace(url, HREF_MAP.get(url));
            }//for
         
            /*
            Ajusta os links para subsecoes e subforuns
            */
            for(String section: sections)
            {
                String firstPage = section.replace("/", "page0.html");
                htmlContent = htmlContent.replace(section, firstPage);
            }
            
            /*
            Converte todos os links para topicos a uma mesma sintaxe
            */
            htmlContent = htmlContent.replace("href=\"topic", "href=\"-t"); 
            
            /*
            Normaliza links para posts
            */
            htmlContent = 
                htmlContent.replaceAll("\\.html&amp;p=\\d+#p", ".html#p");
            
            /*
            Faz com que o script que acessa paginas de topicos pela numeracao
            funcione na copia estatica.
           
            htmlContent = 
                htmlContent.replace
                (
                    "var seo_delim_start = '-';",
                    "var seo_delim_start = '';"
                );
            */

            /*
            Grava o conteudo editado do arquivo de volta para o proprio arquivo
            */
            try { writeTextFile(htmlFile, htmlContent); }
            catch(IOException e) { systemErrPrintln(e.getMessage()); break; }
            
        }//for
        
        
    }//editHtml()
    
    public static void main(String[] args)
    {
       
        editHtml();
     
    }
    
   
    
    private static class FileFilter implements FilenameFilter
    {
        @Override
        public boolean accept(File dir, String filename)
        {
            return filename.contains("page0.html");
        }
    }
      
}//classe Build
