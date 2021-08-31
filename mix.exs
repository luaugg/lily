defmodule LilyEx.MixProject do
  use Mix.Project

  def project do
    [
      app: :lily_ex,
      version: "0.1.0",
      elixir: "~> 1.12",
      start_permanent: Mix.env() == :prod,
      deps: deps()
    ]
  end

  def application do
    [
      extra_applications: [:logger],
      mod: {Lily, []}
    ]
  end

  defp deps do
    [{:nostrum, github: "Kraigie/nostrum"}]
  end
end
