package za.co.pms.model.settings.localization;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

/**
 * @author NMMkhungo
 * @since 2025/09/28
 **/
@Getter
@Setter
@Entity
@Table(name = "language")
@AllArgsConstructor
@NoArgsConstructor
public class Language {
    @Id
    @Column(name = "language_code", length = 10)
    private String code; // en, zu, af, etc.

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "native_name", nullable = false)
    private String nativeName;

    @Column(name = "rtl", nullable = false)
    private Boolean rtl;

    @Column(name = "is_default", nullable = false)
    private Boolean isDefault;

    @OneToMany(mappedBy = "language", cascade = CascadeType.ALL)
    private Set<LanguageRegion> languageRegions = new HashSet<>();

    // Language Entity
    public Language(String code, String name, String nativeName, Boolean rtl, Boolean isDefault) {
        this.code = code;
        this.name = name;
        this.nativeName = nativeName;
        this.rtl = rtl;
        this.isDefault = isDefault;
    }
}
