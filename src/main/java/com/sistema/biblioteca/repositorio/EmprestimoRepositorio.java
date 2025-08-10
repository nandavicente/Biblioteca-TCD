package com.sistema.biblioteca.repositorio;

import com.sistema.biblioteca.entidade.Emprestimo;

public class EmprestimoRepositorio extends RepositorioBase<Emprestimo> {

    @Override
    protected Long getId(Emprestimo obj) {
        return obj.getId();
    }

    @Override
    protected void setNaLixeira(Emprestimo obj, boolean naLixeira) {
        obj.setNaLixeira(naLixeira);
    }

    @Override
    protected boolean isNaLixeira(Emprestimo obj) {
        return obj.isNaLixeira();
    }
}
