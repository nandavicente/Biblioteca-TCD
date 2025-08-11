package com.sistema.biblioteca.repositorio;

import java.util.List;

public interface IRepositorio<T> {
    void salvar(T obj);
    void atualizar(T obj);
    void excluir(Long id);
    T buscarPorId(Long id);
    List<T> buscarTodos();

    // Métodos da Lixeira
    void moverParaLixeira(T obj);
    void moverParaLixeiraPorId(Long id);
    void moverColecaoParaLixeira(List<T> lista);
    List<T> recuperarTodosDaLixeira();
    T recuperarDaLixeiraPorId(Long id);
    void restaurarPorId(Long id);
    void restaurarTodosDaLixeira();
    void excluirDefinitivo(T obj);
    void excluirDefinitivoPorId(Long id);
    void esvaziarLixeira();
}
