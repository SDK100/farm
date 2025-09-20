#version 333 core
layout (location=0) in vec3 aPos;
layout (location=1) in vec3 aNormal;
layout (location=2) in vec2 aUV;

uniform mat4 uModel;
uniform mat4 uView;
uniform mat4 uProj;

out vec3 vWorldPos;
out vec3 vNormal;
out vec2 vUV;

void main(){
	vec4 world = uModel * vec4(aPos, 1.0);
	vWorldPos = world.xyz;
	vNormal = mat3(uModel) * aNormal;
	vUV = aUV;
	gl_position = uProj * uView * world;
}