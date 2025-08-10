package com.sistema.biblioteca.repositorio;

import java.util.ArrayList;
import java.util.List;

public abstract class RepositorioBase<T> implements IRepositorio<T> {
    protected List<T> dados = new ArrayList<>();

    protected abstract Long getId(T obj);
    protected abstract void setNaLixeira(T obj, boolean naLixeira);
    protected abstract boolean isNaLixeira(T obj);

    @Override
    public void salvar(T obj) {
        dados.add(obj);
    }

    @Override
    public void atualizar(T obj) {
        Long id = getId(obj);
        for (int i = 0; i < dados.size(); i++) {
            if (getId(dados.get(i)).equals(id)) {
                dados.set(i, obj);
                return;
            }
        }
    }

    @Override
    public void excluir(Long id) {
        dados.removeIf(obj -> getId(obj).equals(id));
    }

    @Override
    public T buscarPorId(Long id) {
        return dados.stream()
                .filter(obj -> getId(obj).equals(id) && !isNaLixeira(obj))
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<T> buscarTodos() {
        List<T> ativos = new ArrayList<>();
        for (T obj : dados) {
            if (!isNaLixeira(obj)) {
                ativos.add(obj);
            }
        }
        return ativos;
    }

    // Lixeira
    @Override
    public void moverParaLixeira(T obj) {
        setNaLixeira(obj, true);
    }

    @Override
    public void moverParaLixeiraPorId(Long id) {
        for (T obj : dados) {
            if (getId(obj).equals(id)) {
                setNaLixeira(obj, true);
            }
        }
    }

    @Override
    public void moverColecaoParaLixeira(List<T> lista) {
        for (T obj : lista) {
            setNaLixeira(obj, true);
        }
    }

    @Override
    public List<T> recuperarTodosDaLixeira() {
        List<T> lixo = new ArrayList<>();
        for (T obj : dados) {
            if (isNaLixeira(obj)) {
                lixo.add(obj);
            }
        }
        return lixo;
    }

    @Override
    public T recuperarDaLixeiraPorId(Long id) {
        for (T obj : dados) {
            if (getId(obj).equals(id) && isNaLixeira(obj)) {
                return obj;
            }
        }
        return null;
    }

    @Override
    public void excluirDefinitivo(T obj) {
        dados.remove(obj);
    }

    @Override
    public void excluirDefinitivoPorId(Long id) {
        dados.removeIf(obj -> getId(obj).equals(id) && isNaLixeira(obj));
    }

    @Override
    public void esvaziarLixeira() {
        dados.removeIf(this::isNaLixeira);
    }
}
