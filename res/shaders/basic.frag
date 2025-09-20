#version 330 core
in vec3 vColor;
in vec3 vNormal;
in vec3 vWorldPos;

out vec4 FragColor;

uniform vec3 uSunDir = normalize(vec3(-0.3, -1.0, -0.4));
uniform vec3 uSunColor = vec3(1.0, 0.97, 0.92);
uniform float uAmbient = 0.25;

void main() {
    vec3 n = normalize(vNormal);
    float ndl = max(dot(-uSunDir, n), 0.0);
    vec3 lit = vColor * (uAmbient + ndl) * uSunColor;
    FragColor = vec4(lit, 1.0);
}
