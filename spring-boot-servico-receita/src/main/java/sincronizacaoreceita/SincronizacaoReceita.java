/*
Cenário de Negócio:
Todo dia útil por volta das 6 horas da manhã um colaborador da retaguarda do Sicredi recebe e organiza as informações de contas para enviar ao 
Banco Central. Todas agencias e cooperativas enviam arquivos Excel à Retaguarda. Hoje o Sicredi já possui mais de 4 milhões de contas ativas.
Esse usuário da retaguarda exporta manualmente os dados em um arquivo CSV para ser enviada para a Receita Federal, antes as 10:00 da manhã 
na abertura das agências.

Requisito:
Usar o "serviço da receita" (fake) para processamento automático do arquivo.

Funcionalidade:
0. Criar uma aplicação SprintBoot standalone. Exemplo: java -jar SincronizacaoReceita <input-file>
1. Processa um arquivo CSV de entrada com o formato abaixo.
2. Envia a atualização para a Receita através do serviço (SIMULADO pela classe ReceitaService).
3. Retorna um arquivo com o resultado do envio da atualização da Receita. Mesmo formato adicionando o resultado em uma nova coluna.


Formato CSV:
agencia;conta;saldo;status
0101;12225-6;100,00;A
0101;12226-8;3200,50;A
3202;40011-1;-35,12;I
3202;54001-2;0,00;P
3202;00321-2;34500,00;B
...

*/

package sincronizacaoreceita;
 
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
 
@SpringBootApplication
public class SincronizacaoReceita implements CommandLineRunner {
	
	public static void main(String[] args) throws IOException {
		
		SpringApplication app = new SpringApplication(SincronizacaoReceita.class);
        app.run(args);
        
        String caminho = args[0];

		String linha = "";

		// Exemplo como chamar o "serviço" do Banco Central.
		ReceitaService receitaService = new ReceitaService();
		
		//Test receitaService = new Test();
		
		try {
			
			//Construtor que recebe o objeto do tipo arquivo
			FileReader fileReader = new FileReader(caminho);
			
			//Construtor que recebe o objeto do tipo FileReader
			BufferedReader br = new BufferedReader(fileReader);
			String cabecalho  = br.readLine();
			
			String novoArquivo = "/Users/Public/Documents/NovoServicoReceitaSincronizacao.csv";
			
			//Escreve no arquivo
			FileWriter fileWriter = new FileWriter(novoArquivo, true);			
			fileWriter.write(cabecalho);
			fileWriter.write(";");
			fileWriter.write("resultado");
			fileWriter.write("\n");

			//Enquanto houver mais linhas e não for nulo, lê a proxima linha
			while ((linha = br.readLine()) != null) {
				
				//Cria um array de string, recebendo o
				//o valor dividido pelo delimitador ";"
				String[] values = linha.split(";");   

				try {

					System.out.println(values[0] + ", " + values[1] + ", " + values[2] + ", " + values[3]);

					//Variável que pegar o resultado "verdadeiro" ou "falso" do método "atualizarConta();"
					boolean resultadoServico = receitaService.atualizarConta(values[0],
							                                                 values[1],
							                                                 Double.parseDouble(values[2].replaceAll(",", ".")),
							                                                 values[3]);
					//Escreve no arquivo
			       	fileWriter.write(String.valueOf(values[0]));
					fileWriter.write(";");
					fileWriter.write(String.valueOf(values[1]));
					fileWriter.write(";");
					fileWriter.write(String.valueOf(values[2]));
					fileWriter.write(";");
					fileWriter.write(String.valueOf(values[3]));
					fileWriter.write(";");
					fileWriter.write(String.valueOf(resultadoServico));
					fileWriter.write("\n");
					
					System.out.println(String.valueOf(values[0] + ";") 
					      	         + String.valueOf(values[1]+ ";")
					      	         + String.valueOf(values[2]+ ";")
					       	         + String.valueOf(values[3]+ ";")
					       	         + String.valueOf(resultadoServico) + "\n");
					
					//Escreve obrigatoriamente os dados no arquivo
					fileWriter.flush();			
			       	

				} catch (InterruptedException e) {
					System.out.println("Erro de interrupção no processo: \n" + e.getMessage());
				} 
			}			
			
			System.out.println("o novo arquivo com nome 'ResultadoSincronizacaoReceita.csv'"
			         + " foi salvo em '/Users/Public/Documents/NovoServicoReceitaSincronizacao.csv'");

		} catch (RuntimeException e) {
			System.out.println("Erro ao executar o processo: \n" + e.getMessage());
		} catch (FileNotFoundException e) {
			System.out.println("Arquivo não encontrado: \n" + e.getMessage());
		} catch (IOException e) {
			System.out.println("Erro de entrada de dados: \n" + e.getMessage());
		} 
	}
	
	@Override
    public void run(String... arg0) throws Exception { 
        // TODO Auto-generated method stub
        System.out.println("A aplicação spring boot servico receita está funcionando...");
    }

}

