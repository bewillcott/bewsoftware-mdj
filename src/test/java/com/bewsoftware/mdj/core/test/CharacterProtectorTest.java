
package com.bewsoftware.mdj.core.test;

import com.bewsoftware.mdj.core.utils.CharacterProtector;
import java.util.Collection;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 *
 * @author alexbcoles
 *
 * @since 0.6.7
 * @version 0.8.0
 */
public class CharacterProtectorTest
{
    private CharacterProtector characterProtector;

    @BeforeEach
    public void createCharacterProtector()
    {
        characterProtector = new CharacterProtector();
    }

    @Test
    public void testEncodeAndDecodeRoundtrip()
    {
        final String encoded = characterProtector.encode("<h4>Warnemünde</h4>");
        assertEquals("<h4>Warnemünde</h4>", characterProtector.decode(encoded));
    }

    @Test
    public void testGetAllEncodedTokens()
    {
        final Collection<String> tokens = characterProtector.getAllEncodedTokens();
        assertEquals(0, tokens.size());

        characterProtector.encode("<nav><div></div></nav>");
        characterProtector.encode("<h1 id='heading'>Schifffahrt nach Warnemünde</h1>");
        characterProtector.encode("<br/>");
        assertEquals(3, tokens.size());
    }

    @Test
    @SuppressWarnings("ThrowableResultIgnored")
    public void testGetAllEncodedTokensCanNotModified1()
    {
        final Collection<String> tokens = characterProtector.getAllEncodedTokens();

        assertThrows(UnsupportedOperationException.class, () -> tokens.clear(),
                "Trying to modify an 'unmodifiableSet'");
    }

    @Test
    @SuppressWarnings("ThrowableResultIgnored")
    public void testGetAllEncodedTokensCanNotModified2()
    {
        final Collection<String> tokens = characterProtector.getAllEncodedTokens();

        assertThrows(UnsupportedOperationException.class, () ->
        {
            tokens.add("another_token");
            tokens.remove("another_token");
        }, "Trying to modify an 'unmodifiableSet'");
    }
}
