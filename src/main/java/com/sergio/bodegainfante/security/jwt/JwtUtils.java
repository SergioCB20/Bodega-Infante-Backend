package com.sergio.bodegainfante.security.jwt;

import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.sergio.bodegainfante.security.UserDetailsImpl;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import io.github.cdimascio.dotenv.Dotenv;

@Component
public class JwtUtils {

    private static String SECRET_KEY;

    static {
        Dotenv dotenv = Dotenv.load();
        SECRET_KEY = dotenv.get("JWT_SECRET_KEY");
    }

    public String generateJwtToken(Authentication authentication) {
        UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();
        try {
            // Convertir las autoridades a una lista de cadenas (roles)
            List<String> roles = userPrincipal.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)  // Obtener el valor de la autoridad (ROLE_CUSTOMER)
                    .collect(Collectors.toList());

            JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                    .subject(userPrincipal.getUsername())// El nombre de usuario (subject)
                    .issueTime(new Date())  // Hora de emisión
                    .expirationTime(new Date(System.currentTimeMillis() + 86400000))  // 24 horas de expiración
                    .claim("roles", roles)  // Pasar los roles como lista de cadenas
                    .build();

            // Crear el JWT firmado
            SignedJWT signedJWT = new SignedJWT(
                    new JWSHeader(com.nimbusds.jose.JWSAlgorithm.HS256),
                    claimsSet);

            // Firmar el JWT con el SECRET_KEY
            MACSigner signer = new MACSigner(SECRET_KEY);
            signedJWT.sign(signer);

            // Serializar y devolver el token
            return signedJWT.serialize();
        } catch (JOSEException e) {
            throw new RuntimeException("Error while generating JWT", e);
        }
    }

    public boolean validateToken(String token) {
        try {
            // Parsear el token
            SignedJWT signedJWT = SignedJWT.parse(token);


            // Verificar la firma del JWT
            MACVerifier verifier = new MACVerifier(SECRET_KEY);
            if (!signedJWT.verify(verifier)) {
                return false;  // Firma inválida
            }

            // Verificar la fecha de expiración
            JWTClaimsSet claims = signedJWT.getJWTClaimsSet();
            Date expirationTime = claims.getExpirationTime();
            if (expirationTime == null || expirationTime.before(new Date())) {
                return false;  // Token expirado
            }

            return true;
        } catch (Exception e) {
            return false;  // Error al parsear o verificar el token
        }
    }

    public String getUsernameFromToken(String token) {
        try {
            SignedJWT signedJWT = SignedJWT.parse(token);
            JWTClaimsSet claims = signedJWT.getJWTClaimsSet();
            return claims.getSubject();
        } catch (Exception e) {
            throw new RuntimeException("Error while extracting username from JWT", e);
        }
    }

    public List<String> getRolesFromToken(String token) {
        try {
            SignedJWT signedJWT = SignedJWT.parse(token);
            JWTClaimsSet claimsSet = signedJWT.getJWTClaimsSet();
            return claimsSet.getStringListClaim("roles");  // Obtener la lista de roles
        } catch (Exception e) {
            throw new RuntimeException("Error parsing JWT token", e);
        }
    }

}



