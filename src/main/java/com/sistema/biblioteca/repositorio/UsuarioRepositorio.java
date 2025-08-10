package com.sistema.biblioteca.repositorio;

import com.sistema.biblioteca.entidade.Usuario;

public class UsuarioRepositorio extends RepositorioBase<Usuario> {

    @Override
    protected Long getId(Usuario obj) {
        return obj.getId();
    }

    @Override
    protected void setNaLixeira(Usuario obj, boolean naLixeira) {
        obj.setNaLixeira(naLixeira);
    }

    @Override
    protected boolean isNaLixeira(Usuario obj) {
        return obj.isNaLixeira();
    }
}
