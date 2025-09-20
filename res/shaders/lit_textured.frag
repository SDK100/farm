#version 330 core
in vec3 vNormal;
in vec2 vUV;

out vec4 FragColor;

uniform vec3 uSunDir;
uniform vec3 uSunColor;
uniform float uAmbient;
uniform sampler2D uAlbedo;
uniform vec3 uTint;

void main() {
    vec3 n = normalize(vNormal);
    float ndl = max(dot(-uSunDir, n), 0.0);
    vec3 base = texture(uAlbedo, vUV).rgb * uTint;
    vec3 lit = base * (uAmbient + ndl) * uSunColor;
    FragColor = vec4(lit, 1.0);
}
