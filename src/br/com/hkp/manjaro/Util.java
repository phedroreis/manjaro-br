package br.com.hkp.manjaro;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.HashSet;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/******************************************************************************
 * A classe fornece metodos utilitarios
 * 
 * @since 5 de março de 2021 v1.0
 * @version 1.0
 * @author "Pedro Reis"
 *****************************************************************************/
public final class Util
{
    private static final String ANSI_RESET = "\u001B[0m";
    private static final String ANSI_BLACK = "\u001B[30m";
    private static final String ANSI_RED = "\u001B[31m";
    private static final String ANSI_GREEN = "\u001B[32m";
    private static final String ANSI_CYAN = "\u001B[36m";

    private static final String ANSI_WHITE_BACKGROUND = "\u001B[47m";
    
    public static String defaultColor;
    public static String black;
    public static String red;
    public static String green;
    public static String cyan;
  
    public static String whiteBackground;
    
    public static final String PREFIX = 
        "https://www.manjaro-linux.com.br/forum/";
    
    public static final String DOWNLOAD_DIR = "./manjaro-linux.com.br/forum";
    
    private static final Pattern NUMBER_OF_PAGES = 
        Pattern.compile("P\u00e1gina <b>\\d+<\\/b> de <b>(\\d+)<\\/b>");
    
    public static final HashSet<String> DOWNLOADEDS = new HashSet<>();
    
    /*[01]---------------------------------------------------------------------
    
    -------------------------------------------------------------------------*/
    /**
     * Recebe o conteudo da 1a pagina de um topico ou subsecao, e retorna 
     * quantas paginas (arquivos) constituem aquele topico ou subsecao.
     * 
     * @param contentFile Todo o conteudo do arquivo.
     * 
     * @return true se o metodo conseguiu decodificar o numero de paginas.
     * 
     * @throws BadFileFormatException Se o metodo falhou em decodificar o 
     * numero de paginas daquele topico ou subsecao.
     */
    public static int numberOfPages(final String contentFile)
        throws BadFileFormatException
    {
        Matcher m = NUMBER_OF_PAGES.matcher(contentFile);

        if (m.find())
            return  (Integer.valueOf(m.group(1)) - 1); 
        else
            throw new BadFileFormatException();
    }//numberOfPages()
    
    /*[02]---------------------------------------------------------------------
    
    -------------------------------------------------------------------------*/
    /**
     * Le um arquivo no sistema de arquivos para um objeto String e a retorna.
     * Este arquivo deve estar codificado em UTF-8.
     * 
     * @param file O arquivo a ser lido.
     * 
     * @return Uma String com o conteudo do arquivo texto.
     * 
     * @throws IOException Em caso de erro de IO.
     */
    public static String readTextFile(final File file) throws IOException
    {
        return 
            new String(Files.readAllBytes(file.toPath()), StandardCharsets.UTF_8);
       
    }//readTextFile()
    
    /*[03]---------------------------------------------------------------------
    
    -------------------------------------------------------------------------*/
    /**
     * Le um arquivo no sistema de arquivos para um objeto String e a retorna.
     * Este arquivo deve estar codificado em UTF-8.
     * 
     * @param filename O nome do arquivo a ser lido.
     * 
     * @return Uma String com o conteudo do arquivo texto.
     * 
     * @throws IOException Em caso de erro de IO.
     */
    public static String readTextFile(final String filename) throws IOException
    {
        return readTextFile(new File(filename));
    }//readTextFile()
    
    /*[04]---------------------------------------------------------------------
    
    -------------------------------------------------------------------------*/
    /**
     * Escreve o conteudo de uma String em um arquivo texto. Se o arquivo jah
     * existir seu conteudo serah substituido por esta String, e se nao existir
     * serah criado. A String deve ser UTF0.
     * 
     * @param file O arquivo.
     * 
     * @param content A String codificada em UTF-8.
     * 
     * @throws IOException Em caso de erro de IO.
     */
    public static void writeTextFile(final File file, final String content)
        throws IOException
    {
        FileWriter  fw = new FileWriter(file, StandardCharsets.UTF_8);
               
        fw.write(content);
        
        fw.close();
  
    }//writeTextFile()
    
    /*[05]---------------------------------------------------------------------
    
    -------------------------------------------------------------------------*/
    /**
     * Escreve o conteudo de uma String em um arquivo texto. Se o arquivo jah
     * existir seu conteudo serah substituido por esta String, e se nao existir
     * o arquivo serah criado. A String deve ser UTF8.
     * 
     * @param filename O arquivo.
     * 
     * @param content A String codificada em UTF-8.
     * 
     * @throws IOException Em caso de erro de IO.
     */
    public static void writeTextFile(final String filename, final String content)
        throws IOException
    {
        writeTextFile(new File(filename), content);
    }//writeTextFile()
    
    /*[06]---------------------------------------------------------------------
    
    -------------------------------------------------------------------------*/
    /*
     * Caso ocorra uma IOException durante o download de um arquivo HTML que 
     * deveria ser baixado, este metodo informa o erro no terminal e solicita 
     * ao usuario que decida se o programa deve tentar de novo.
     * 
     * @return true se o usuario decidiu tentar mais uma vez baixar o arquivo e
     * false se decidiu abortar.
     */
    
    private static final Scanner SCANNER = new Scanner(System.in);
    
    private static boolean retry()
    {
        char option = '.';

        while((option != 's') && (option != 'n'))
        {
            System.out.println
            (
                red + whiteBackground + "\nNova tentativa? [S/n]\n" + defaultColor
            );
            String scan = SCANNER.next();
            option = scan.toLowerCase().charAt(0);
        }

        return (option == 's'); 
    }//retry()
 
    /*[07]---------------------------------------------------------------------
    
    -------------------------------------------------------------------------*/
    /**
     * Baixa um arquivo apontado por uma URL para um diretorio especificado que
     * jah deve indicar tambem o nome do arquivo que serah gravado.
     * 
     * @param url A url do arquivo a ser baixado.
     * 
     * @param pathname Caminho absoluto ou relativo para onde gravar o arquivo,
     * incluindo tambem o nome que serah dado ao arquivo baixado.
     * 
     * @return true se o download teve sucesso, false se nao
     * 
     */
    @SuppressWarnings("ConvertToTryWithResources")
    public static boolean downloadUrl2Pathname
    (
        final String url, 
        final String pathname
    )
    {
        FileOutputStream fos = null;
        
        boolean retry = true;
        
        int ioErrors = 0;
       
        while (retry)
        {
            retry = false;
            
            try
            {
                URL download = new URL(url);

                ReadableByteChannel rbc = 
                    Channels.newChannel(download.openStream());

                fos = new FileOutputStream(pathname);

                fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
                
            }
            catch (FileNotFoundException e)
            {
                systemErrPrintln(e.toString());
                return false;
            }
            catch (IOException e)
            {
                systemErrPrintln(e.toString());
                if (ioErrors < 3)
                {
                    ioErrors++;
                    retry = true;
                }
                else
                {
                    ioErrors = 0;
                    if (retry()) 
                        retry = true;
                    else
                        return false;
                }
            }
            finally
            {
               try 
               {
                   if (fos != null) fos.close(); 
               }
               catch (IOException e)
               {
                   systemErrPrintln(e.toString());
                   return false;
               }
            }//try-catch
            
        }//while
        
        return true;
        
    }//downloadUrlWithPathname()
    
    /*[08]---------------------------------------------------------------------
    
    -------------------------------------------------------------------------*/
    /**
     * Obtem apenas o nome de um arquivo a partir da sua URL.
     * 
     * @param url A URL. (A URL deve indicar um arquivo e nao um site somente)
     * 
     * @return O nome do arquivo.
     */
    public static String extractFilenameFromUrl(final String url)
    {
        int lastIndexOf = url.lastIndexOf('/');
        if (lastIndexOf == -1) return url;
        return url.substring(lastIndexOf + 1, url.length());
    }//extractFilenameFromUrl()
     
    /*[09]---------------------------------------------------------------------
    
    -------------------------------------------------------------------------*/
    /**
     * Baixa um arquivo para um diretorio especificado.O nome do arquivo serah
     * mantido como o mesmo desta URL. Por exemplo: 
     * https://www.nomeDeDominio/caminho/nomeArquivo.txt este 
     * arquivo seria gravado com nome "nomeArquivo.txt" no diretorio
     * especificado.
     * 
     * @param url A URL do arquivo.
     * 
     * @param path O caminho do diretorio onde gravar o arquivo. Pode ser 
     * relativo ou absoluto.
     * 
     * @return true se o download teve sucesso, false se nao
     * 
     */
    public static boolean downloadUrl(final String url, final String path)
    {
        String pathname = path + '/' + extractFilenameFromUrl(url);
        
        return downloadUrl2Pathname(url, pathname);
               
    }//downloadUrl()
    
    /*[10]---------------------------------------------------------------------
    
    -------------------------------------------------------------------------*/
    /**
     * Le um arquivo txt empacotado no jar.
     * 
     * @param filename O nome do arquivo empacotado no jar.
     * 
     * @return O conteudo do arquivo.
     * 
     */
    public static String getTxtResource(final String filename)
    {
        StringBuilder sb = new StringBuilder();
        try
        {    
            InputStream in = 
                new Util().getClass().getResourceAsStream(filename); 
            
            BufferedReader reader = 
                new BufferedReader
                    (
                        new InputStreamReader(in, StandardCharsets.UTF_8)
                    );
            
            String line;
            while ((line = reader.readLine()) != null) 
                sb.append(line).append("\n");
            sb.append("\n");
        }
        catch (IOException e) {}
        
        return sb.toString();
    }//getTxtResource()
    
    /*[11]---------------------------------------------------------------------
    
    -------------------------------------------------------------------------*/
    /**
     * Exibe informacoes iniciais no console ao se executar o programa.
     * 
     * @param filename O arquivo de onde eh lido o texto da mensagem.
     */
    public static void showOpenMessage(final String filename) 
    {
        System.out.println(getTxtResource(filename));
    }//showOpenMessage()
    
    /*[12]---------------------------------------------------------------------
    
    -------------------------------------------------------------------------*/
    /**
     * Imprime mensagem de erro no console em letras vermelhas
     * 
     * @param err Mensagem de erro
     */
    public static void systemErrPrint(String err)
    {
        System.err.print(red + err + defaultColor);
    }//systemErrPrint()
    
    /*[13]---------------------------------------------------------------------
    
    -------------------------------------------------------------------------*/
    /**
     * Imprime mensagem de erro no console (com quebra de linha) em letras 
     * vermelhas
     * 
     * @param err Mensagem de erro
     */
    public static void systemErrPrintln(String err)
    {
        System.err.println(red + err + defaultColor);
    }//SystemErrPrintln()
    
    /*[14]---------------------------------------------------------------------
    
    -------------------------------------------------------------------------*/
    /**
     * Se o programa executar em ambientes linux a saida no terminal serah 
     * colorizada.
     */
    public static void setTerminalColors()
    {
        if (System.getProperty("os.name").equalsIgnoreCase("linux"))
        {
            defaultColor = ANSI_RESET;
            black = ANSI_BLACK;
            red = ANSI_RED;
            green = ANSI_GREEN;
            cyan = ANSI_CYAN;

            whiteBackground = ANSI_WHITE_BACKGROUND;
        }
        else
        {
            defaultColor = "";
            black = "";
            red = "";
            green = "";
            cyan = "";

            whiteBackground = "";
        }//if-else
    }//setTerminalColors()
    
    /*[15]---------------------------------------------------------------------
    
    -------------------------------------------------------------------------*/
    public static boolean downloadUrlMakingDirs(final String url)
    {
        int lastIndexOf = url.lastIndexOf('/');
        String path;
        if (lastIndexOf != -1)
        {
            path = DOWNLOAD_DIR + '/' + url.substring(0, lastIndexOf);
            File dir = new File(path);
            if (!dir.exists()) dir.mkdirs();
        }
        else
            path = DOWNLOAD_DIR;
        String pathname = path + '/' + extractFilenameFromUrl(url);
        
        if (DOWNLOADEDS.contains(pathname))
            return true;
        else
        {
            DOWNLOADEDS.add(pathname);

            return downloadUrl(PREFIX + url, path);
        }
      
    }//downloadUrlMakingDirs
    
    /**************************************************************************
                                 Classe interna
    ***************************************************************************/
    public static class BadFileFormatException extends FileNotFoundException
    {
        /*[01]------------------------------------------------------------------

        ----------------------------------------------------------------------*/
        public BadFileFormatException()
        {
            super("");
        }//construtor
        
        /*[02]------------------------------------------------------------------

        ----------------------------------------------------------------------*/
        /**
         * Quando um arquivo HTML do forum eh baixado mas nao eh possivel 
         * interpretar seu conteudo por nao estar no formato esperado, este 
         * metodo verifica se isto ocorreu porque foi recebida uma pagina de
         * autenticacao ou porque a formatacao do arquivo nao eh a esperada.O 
         * metodo printa no terminal a informacao adequada.
         * 
         * @param fileContent Todo o conteudo do arquivo HTML que foi lido.
         * 
         * @param url
         */
        public void errMessage
        (
            final String fileContent,
            final String url
        )
        {
            systemErrPrint("Bad File - ");
            if (fileContent.contains("esteja registrado e autenticado"))
                systemErrPrint("Authentication page: ");
            else
                systemErrPrint("Unknow file: ");
            systemErrPrintln(url);

        }//printBadFileMsg()
        
    }//classe BadFileFormatException
    
    /***************************************************************************
     *      Classe interna. Um filtro que seleciona apenas arquivos HTML.
     **************************************************************************/
    public static class HtmlFilter implements FilenameFilter
    {
        @Override
        public boolean accept(File dir, String filename)
        {
            return filename.endsWith(".html");
        }//accetp()
    }//classe HtmlFilter
    
    public static void main(String[] args)
    {
        showOpenMessage("getTopicsMsg.txt");
    }
    
 }//classe Util

