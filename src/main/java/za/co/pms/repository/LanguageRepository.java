package za.co.pms.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import za.co.pms.model.settings.localization.Language;

import java.util.List;
import java.util.Optional;

/**
 * @author NMMkhungo
 * @since 2025/09/28
 **/
@Repository
public interface LanguageRepository extends JpaRepository<Language, String> {
    @Query("SELECT l FROM Language l WHERE l.isDefault = true")
    Optional<Language> findDefaultLanguage();

    @Query("SELECT l FROM Language l JOIN l.languageRegions lr WHERE lr.regionCode = :regionCode")
    List<Language> findByRegionCode(@Param("regionCode") String regionCode);

    @Query("SELECT l FROM Language l WHERE l.rtl = :rtl")
    List<Language> findByRtl(@Param("rtl") Boolean rtl);
}
