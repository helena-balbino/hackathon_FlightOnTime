const form = document.getElementById("predictForm");
const resultCard = document.getElementById("result");

const labelBadge = document.getElementById("labelBadge");
const probaBadge = document.getElementById("probaBadge");
const globalInsight = document.getElementById("globalInsight");

let chartLocal = null;

const LINE_TYPE_LABEL = {
  N: "Doméstica Mista",
  C: "Doméstica Cargueira",
  I: "Internacional Mista",
  G: "Internacional Cargueira",
};

function formatLabel(label) {
  return label === "atrasado" ? "Atrasado" : "No Prazo";
}

function setLabelStyle(label) {
  if (label === "atrasado") {
    labelBadge.style.background = "#ffe4e6";
    labelBadge.style.color = "#9f1239";
    labelBadge.style.borderColor = "rgba(159,18,57,0.18)";
  } else {
    labelBadge.style.background = "#dcfce7";
    labelBadge.style.color = "#166534";
    labelBadge.style.borderColor = "rgba(22,101,52,0.18)";
  }
}

function normalizeFeatureName(feature) {
  if (!feature) return "";
  // ex: cat__codigo_tipo_linha_N -> codigo_tipo_linha_N
  return feature.replace(/^cat__|^num__/g, "");
}

function buildGlobalInsight(explainGlobal, formData) {
  if (!explainGlobal || !Array.isArray(explainGlobal) || explainGlobal.length === 0) {
    return "A explicabilidade global não está disponível para este modelo.";
  }

  const top1 = explainGlobal[0]?.feature || "";
  const top2 = explainGlobal[1]?.feature || "";

  const f1 = normalizeFeatureName(top1);
  const f2 = normalizeFeatureName(top2);

  const codigoTipoLinha = (formData.get("codigo_tipo_linha") || "N").toString();
  const tipoLinhaDesc = LINE_TYPE_LABEL[codigoTipoLinha] || codigoTipoLinha;

  const mentionsTipoLinha = f1.includes("codigo_tipo_linha") || f2.includes("codigo_tipo_linha");
  const mentionsMediaEmpresa = f1.includes("media_atraso_empresa") || f2.includes("media_atraso_empresa");

  // Texto corporativo (ajustável)
  const parts = [];

  parts.push(
    `Na leitura global do modelo, os fatores mais relevantes para a previsão de atraso são relacionados ao perfil operacional do voo e ao histórico médio de atraso associado à companhia aérea.`
  );

  if (mentionsTipoLinha) {
    parts.push(
      `Em especial, o tipo de linha (ex.: ${codigoTipoLinha} – ${tipoLinhaDesc}) aparece como um dos principais direcionadores do comportamento do modelo, indicando que o contexto operacional (doméstico/internacional e passageiro/carga) influencia significativamente o risco estimado.`
    );
  }

  if (mentionsMediaEmpresa) {
    parts.push(
      `Além disso, a média histórica de atraso da companhia atua como um indicador agregado de confiabilidade operacional, reforçando que padrões recorrentes de performance tendem a se refletir nas probabilidades previstas pelo modelo.`
    );
  }

  // Se por algum motivo não bater com esses nomes, ainda assim devolve algo bom:
  if (!mentionsTipoLinha && !mentionsMediaEmpresa) {
    const safe1 = (top1 || "variável 1").replaceAll("_", " ");
    const safe2 = (top2 || "variável 2").replaceAll("_", " ");
    parts.push(
      `Neste modelo, as duas variáveis globais mais influentes são: "${safe1}" e "${safe2}".`
    );
  }

  parts.push(
    `Em termos práticos, esse insight pode apoiar decisões operacionais (planejamento, gestão de risco e priorização de monitoramento) ao destacar quais aspectos estruturais mais pesam na previsão.`
  );

  return parts.join(" ");
}

function renderLocalChart(topFeatures) {
  const canvas = document.getElementById("chartLocal");
  const ctx = canvas.getContext("2d");

  const labels = topFeatures.map((f) => f.feature);
  const values = topFeatures.map((f) => f.contribution);

  const colors = values.map((v) => (v >= 0 ? "#22c55e" : "#ef4444"));

  if (chartLocal) chartLocal.destroy();

  chartLocal = new Chart(ctx, {
    type: "bar",
    data: {
      labels,
      datasets: [
        {
          label: "Contribution",
          data: values,
          backgroundColor: colors,
          borderWidth: 0,
        },
      ],
    },
    options: {
      indexAxis: "y",
      responsive: true,
      maintainAspectRatio: false,
      plugins: {
        legend: { display: false },
        tooltip: {
          callbacks: {
            label: (context) => ` ${Number(context.raw).toFixed(4)}`,
          },
        },
      },
      scales: {
        x: {
          title: { display: true, text: "Contribution" },
          grid: { color: "rgba(0,0,0,0.08)" },
        },
        y: {
          grid: { display: false },
        },
      },
    },
  });
}

form.addEventListener("submit", async (e) => {
  e.preventDefault();

  const data = new FormData(form);

  const payload = {
  dados: {
    partida_prevista: (data.get("partida_prevista") || "").trim(),
    empresa_aerea: (data.get("empresa_aerea") || "").trim().toUpperCase(),
    aerodromo_origem: (data.get("aerodromo_origem") || "").trim().toUpperCase(),
    aerodromo_destino: (data.get("aerodromo_destino") || "").trim().toUpperCase(),
    codigo_tipo_linha: (data.get("codigo_tipo_linha") || "").trim().toUpperCase(),
  },
  topk: Number(data.get("topk") || 8),
};

  try {
    const res = await fetch("/predict", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(payload),
    });

    const body = await res.json();

    if (!res.ok) {
      alert("Erro: " + JSON.stringify(body));
      return;
    }

    resultCard.classList.remove("hidden");

    // Badge: Label
    const labelRaw = body.label; // "atrasado" | "no_prazo"
    labelBadge.textContent = `Label: ${formatLabel(labelRaw)}`;
    setLabelStyle(labelRaw);

    // Badge: Probabilidade
    if (body.proba_atraso !== undefined && body.proba_atraso !== null) {
      probaBadge.textContent = `Probabilidade de Atraso: ${(body.proba_atraso * 100).toFixed(2)}%`;
    } else {
      probaBadge.textContent = `Probabilidade de Atraso: —`;
    }

    // Gráfico local (somente)
    const feats = body.explain_local?.top_features || [];
    renderLocalChart(feats);

    // Texto global (duas features principais)
    globalInsight.innerHTML = buildGlobalInsight(body.explain_global, data);

    resultCard.scrollIntoView({ behavior: "smooth" });
  } catch (err) {
    alert("Falha ao chamar a API: " + err);
  }
});
