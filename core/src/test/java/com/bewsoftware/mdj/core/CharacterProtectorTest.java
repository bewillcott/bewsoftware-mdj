package com.bewsoftware.mdj.core;

import java.util.Collection;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 *
 * @author alexbcoles
 */
public class CharacterProtectorTest {

    private CharacterProtector characterProtector;

    @BeforeEach
    public void createCharacterProtector() {
        characterProtector = new CharacterProtector();
    }

    @Test
    public void testEncodeAndDecodeRoundtrip() {
        String encoded = characterProtector.encode("<h4>Warnemünde</h4>");
        assertEquals("<h4>Warnemünde</h4>", characterProtector.decode(encoded));
    }

    @Test
    public void testGetAllEncodedTokens() {
        Collection<String> tokens = characterProtector.getAllEncodedTokens();
        assertEquals(0, tokens.size());

        characterProtector.encode("<nav><div></div></nav>");
        characterProtector.encode("<h1 id='heading'>Schifffahrt nach Warnemünde</h1>");
        characterProtector.encode("<br/>");
        assertEquals(3, tokens.size());
    }

    @Test
    public void testGetAllEncodedTokensCanNotModified1() {
        Collection<String> tokens = characterProtector.getAllEncodedTokens();

        assertThrows(UnsupportedOperationException.class, () -> tokens.clear(),
                     "Trying to modify an 'unmodifiableSet'");
    }

    @Test
    public void testGetAllEncodedTokensCanNotModified2() {
        Collection<String> tokens = characterProtector.getAllEncodedTokens();

        assertThrows(UnsupportedOperationException.class, () ->
             {
                 tokens.add("another_token");
                 tokens.remove("another_token");
             }, "Trying to modify an 'unmodifiableSet'");
    }

}
