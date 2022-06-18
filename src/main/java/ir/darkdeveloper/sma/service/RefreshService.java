package ir.darkdeveloper.sma.service;


import ir.darkdeveloper.sma.model.RefreshModel;
import ir.darkdeveloper.sma.repository.RefreshRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

import static ir.darkdeveloper.sma.utils.Generics.exceptionHandlers;

@Service
@RequiredArgsConstructor
public class RefreshService {

    private final RefreshRepo repo;

    @Transactional
    public void saveToken(RefreshModel model) {
        exceptionHandlers(() -> repo.save(model));
    }

    public RefreshModel updateTokenByUserId(Long userId, String accessToken) {
        return repo.updateTokenByUserId(userId, accessToken);
    }

    @Transactional
    public void deleteTokenByUserId(Long id) {
        repo.deleteTokenByUserId(id);
    }

    public RefreshModel getRefreshByUserId(Long id) {
        return repo.getRefreshByUserId(id);
    }

    public Long getIdByUserId(Long adminId) {
        return repo.getIdByUserId(adminId);
    }

}
