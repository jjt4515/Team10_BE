package poomasi.domain.aftersales.service;

public interface CancelService<T, P> {
    T cancel(P parameter);
}
