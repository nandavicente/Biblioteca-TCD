package com.sistema.biblioteca.entidade;
/** 
 *
 * @author aline
 */
import java.time.LocalDate;

public class Emprestimo {

    private long id;
    private int idLivro;
    private int idUsuario;
    private LocalDate dataEmprestimo;
    private LocalDate dataPrevista;
    private LocalDate dataReal;
    private boolean naLixeira = false;


    public Emprestimo() {
        
    }
    
    public Emprestimo(int idLivro, int idUsuario) {
        this.idLivro = idLivro;
        this.idUsuario = idUsuario;
        this.dataEmprestimo = LocalDate.now();
        this.dataPrevista = this.dataEmprestimo.plusDays(7);
        this.dataReal = null;
    }
    
    //<editor-fold defaultstate="collapsed" desc="Getters/Setters">
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getIdLivro() {
        return idLivro;
    }

    public void setIdLivro(int idLivro) {
        this.idLivro = idLivro;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public LocalDate getDataEmprestimo() {
        return dataEmprestimo;
    }

    public void setDataEmprestimo(LocalDate dataEmprestimo) {
        this.dataEmprestimo = dataEmprestimo;
    }

    public LocalDate getDataPrevista() {
        return dataPrevista;
    }

    public void setDataPrevista(LocalDate dataPrevista) {
        this.dataPrevista = dataPrevista;
    }

    public LocalDate getDataReal() {
        return dataReal;
    }

    public void setDataReal(LocalDate dataReal) {
        this.dataReal = dataReal;
    }

    public boolean isNaLixeira() {
        return naLixeira;
    }

    public void setNaLixeira(boolean naLixeira) {
        this.naLixeira = naLixeira;
    }
    //</editor-fold>

    public void registrarDevolucao() {
        this.dataReal = LocalDate.now();
    }

    // Metodo_para exibir
    @Override
    public String toString() {
        String status;

        if (dataReal != null) {
            if (dataReal.isAfter(dataPrevista)) {
                status = "Devolvido com atraso";
            } else {
                status = "Devolvido";
            }
        } else {
            if (LocalDate.now().isAfter(dataPrevista)) {
                status = "Atrasado";
            } else {
                status = "Dentro do prazo";
            }
        }

        return "Emprestimo{"
                + "id=" + id
                + ", id livro=" + idLivro
                + ", id usuario=" + idUsuario
                + ", data emprestimo=" + dataEmprestimo
                + ", devolução prevista=" + dataPrevista
                + ", status=" + status
                + '}';
    }

}

