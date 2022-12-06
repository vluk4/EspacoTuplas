package br.edu.ifce.space;

import java.rmi.RemoteException;

import net.jini.core.entry.UnusableEntryException;
import net.jini.core.transaction.TransactionException;
import net.jini.space.JavaSpace;

public class EspacoTuplas {
    Lookup finder = null;
    JavaSpace space = null;

    public EspacoTuplas() {
        try {
            System.out.println("Procurando pelo servico JavaSpace...");
            finder = new Lookup(JavaSpace.class);
            space = (JavaSpace) finder.getService();

            if (space == null) {
                System.out.println("O servico JavaSpace nao foi encontrado. Encerrando...");
                System.exit(-1);
            }

            System.out.println("O servico JavaSpace foi encontrado.");
            System.out.println("  ESPACO >>> " + space);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String read(String nome) {
        Message msg = null;
        Message template = new Message();

        try {
            template.nome = nome;
            msg = (Message) space.read(template, null, 500);

            if (msg == null) {
                System.out.println("Objeto não encontrada no espaco...");
                return null;
            }
        } catch (RemoteException | UnusableEntryException | TransactionException | InterruptedException e) {
            e.printStackTrace();
            return null;
        }
        return msg.mensagem;
    }

    public String take(String nome) {
        Message msg = null;
        Message template = new Message();

        try {
            template.nome = nome;
            msg = (Message) space.take(template, null, 500);

            if (msg == null) {
                System.out.println("Objeto não encontrada no espaco...");
                return null;
            }
        } catch (RemoteException | UnusableEntryException | TransactionException | InterruptedException e) {
            e.printStackTrace();
            return null;
        }
        return msg.mensagem;
    }

    public void write(String nome, String mensagem) {
        Message tupla = new Message();
        Message template = new Message();
        template.nome = nome;

        try {
            Message msg = (Message) space.take(template, null, 500);

            tupla.nome = nome;
            tupla.mensagem = mensagem;
            space.write(tupla, null, 60*60*1000);
        } catch (RemoteException | TransactionException | UnusableEntryException | InterruptedException e) {
            e.printStackTrace();
        }
    }

}
