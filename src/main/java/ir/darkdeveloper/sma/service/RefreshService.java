package ir.darkdeveloper.sma.service;


import ir.darkdeveloper.sma.model.RefreshModel;
import ir.darkdeveloper.sma.repository.RefreshRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
public class RefreshService {

    private final RefreshRepo repo;

    @Autowired
    public RefreshService(RefreshRepo repo) {
        this.repo = repo;
    }

    @Transactional
    public RefreshModel saveToken(RefreshModel model) {
        return repo.save(model);
    }

    public RefreshModel updateTokenByUserId(Long userId, String accessToken) {
        return repo.updateTokenByUserId(userId, accessToken);
    }

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
