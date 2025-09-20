#version 330 core
in vec3 vWorldPos;
in vec3 vNormal;
in vec2 vUV;

out vec4 FragColor;

uniform sampler2D uAlbedo;
uniform vec3 uSunDir;    // should be normalized
uniform vec3 uSunColor;
uniform float uAmbient;  // 0..1
uniform vec3 uTint;      // rgb multiplier

void main(){
	
	vec3 N = normalize(vNormal);
	float ndl = max(dot(N, normalize(-uSunDir)), 0.0);
	vec3 albedo = texture(uAlbedo, vUV).rgb * uTint;
	
	vec3 color = albedo * (uAmbient + ndl * uSunColor);
	FragColor = vec4(color,1.0);

}