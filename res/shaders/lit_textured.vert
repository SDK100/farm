#version 330 core
layout (location=0) in vec3 aPos;
layout (location=1) in vec3 aNormal;
layout (location=2) in vec2 aUV;

uniform mat4 uView;
uniform mat4 uProj;
uniform mat4 uModel;

out vec3 vNormal;
out vec2 vUV;

void main() {
    mat3 nmat = mat3(transpose(inverse(uModel)));
    vNormal = normalize(nmat * aNormal);
    vUV = aUV;
    gl_Position = uProj * uView * uModel * vec4(aPos, 1.0);
}
