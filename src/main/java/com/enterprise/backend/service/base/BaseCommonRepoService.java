package com.enterprise.backend.service.base;

import com.enterprise.backend.exception.EnterpriseBackendException;
import com.enterprise.backend.model.entity.Auditable;
import com.enterprise.backend.model.error.ErrorCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;


public abstract class BaseCommonRepoService<E extends Auditable, ID, R extends BaseCommonRepository<E, ID>> {

    protected final R repo;
    protected final Logger log;

    protected BaseCommonRepoService(R repo) {
        log = LoggerFactory.getLogger(getClass());
        this.repo = repo;
    }

    public E save(E entity) {
        return repo.save(entity);
    }

    public List<E> save(List<E> entities) {
        repo.saveAll(entities);
        return entities;
    }

    public Optional<E> get(ID id) {
        return repo.findById(id);
    }

    public List<E> get(List<ID> ids) throws EnterpriseBackendException {
        if (ids.size() > 1000) {
            throw new EnterpriseBackendException(ErrorCode.NOT_FOUND);
        }
        return repo.findByIdIn(ids);
    }

    public E getOrElseThrow(ID id) throws EnterpriseBackendException {
        return get(id)
                .orElseThrow(() -> new EnterpriseBackendException(ErrorCode.NOT_FOUND));
    }

    public E getOrElseThrow(ID id, String message) throws EnterpriseBackendException {
        return get(id)
                .orElseThrow(() -> new EnterpriseBackendException(ErrorCode.NOT_FOUND, message));
    }

    public void delete(E entity) {
        repo.delete(entity);
    }

    public void deleteIfExisted(ID id) {
        Optional<E> optional = get(id);
        if (optional.isEmpty()) {
            return;
        }
        E entity = optional.get();
        delete(entity);
        repo.flush();
    }

    public void deleteAllByIdInBatch(Iterable<ID> ids) {
        repo.deleteAllByIdInBatch(ids);
    }

    public Page<E> query(Pageable pageable) {
        return repo.findAll(pageable);
    }

    public E updateOnField(ID id, Consumer<E> fieldConsumer) throws EnterpriseBackendException {
        E entity = getOrElseThrow(id);
        return update(entity, fieldConsumer);
    }

    public E update(E entity, Consumer<E> fieldConsumer) {
        fieldConsumer.accept(entity);
        save(entity);
        return entity;
    }

    public boolean exits(ID id) {
        return repo.existsById(id);
    }

    public List<E> getAll(List<ID> ids) {
        return repo.findAllById(ids);
    }

    public List<E> getAll() {
        return repo.findAll();
    }

    protected abstract String notFoundMessage();
}
