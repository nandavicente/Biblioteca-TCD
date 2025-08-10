package com.sistema.biblioteca.repositorio;

import com.sistema.biblioteca.entidade.Livro;

public class LivroRepositorio extends RepositorioBase<Livro> {

    @Override
    protected Long getId(Livro obj) {
        return obj.getId();
    }

    @Override
    protected void setNaLixeira(Livro obj, boolean naLixeira) {
        obj.setNaLixeira(naLixeira);
    }

    @Override
    protected boolean isNaLixeira(Livro obj) {
        return obj.isNaLixeira();
    }
}
