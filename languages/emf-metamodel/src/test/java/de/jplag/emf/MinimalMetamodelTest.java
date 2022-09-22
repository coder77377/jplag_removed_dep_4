package de.jplag.emf;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.jplag.Token;
import de.jplag.TokenPrinter;
import de.jplag.TokenType;
import de.jplag.testutils.FileUtil;
import de.jplag.testutils.TokenUtils;

class MinimalMetamodelTest {
    private final Logger logger = LoggerFactory.getLogger("JPlag-Test");

    private static final Path BASE_PATH = Path.of("src", "test", "resources", "de", "jplag", "models");
    private static final String[] TEST_SUBJECTS = {"bookStore.ecore", "bookStoreExtended.ecore", "bookStoreRenamed.ecore"};

    private de.jplag.Language language;
    private File baseDirectory;

    @BeforeEach
    public void setUp() {
        language = new Language();
        baseDirectory = BASE_PATH.toFile();
        FileUtil.assertDirectory(baseDirectory, TEST_SUBJECTS);
    }

    @Test
    void testBookstoreMetamodels() {
        List<Token> result = language.parse(Arrays.stream(TEST_SUBJECTS).map(path -> new File(BASE_PATH.toFile(), path)).collect(Collectors.toSet()));

        logger.debug(TokenPrinter.printTokens(result, baseDirectory, Optional.of(Language.VIEW_FILE_SUFFIX)));
        List<TokenType> tokenTypes = result.stream().map(Token::getType).toList();
        logger.info("Parsed token types: " + tokenTypes.stream().map(TokenType::getDescription).toList().toString());
        assertEquals(43, tokenTypes.size());
        assertEquals(10, new HashSet<>(tokenTypes).size());

        var bookstoreTokens = TokenUtils.tokenTypesByFile(result, TEST_SUBJECTS[0]);
        var bookstoreRenamedTokens = TokenUtils.tokenTypesByFile(result, TEST_SUBJECTS[2]);
        var bookstoreExtendedTokens = TokenUtils.tokenTypesByFile(result, TEST_SUBJECTS[1]);
        assertTrue(bookstoreTokens.size() < bookstoreExtendedTokens.size());
        assertIterableEquals(bookstoreTokens, bookstoreRenamedTokens);
    }

    @AfterEach
    public void tearDown() {
        FileUtil.clearFiles(new File(BASE_PATH.toString()), Language.VIEW_FILE_SUFFIX);
    }

}
